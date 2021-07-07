# Spring-Security微服务解决方案

## 1.注册中心的服务保护

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-security</artifactId>
</dependency>
```

```java
@EnableWebSecurity
public class IMoodRegisterWebSecurityConfigure extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().ignoringAntMatchers("/eureka/**");
        super.configure(http);
    }
}
```

application.yml

```yml
spring:
  security:
    user:
      name: imood
      password: 123456
eureka:
  instance:
#    hostname: localhost
    hostname: ${imood-register}
  client:
    register-with-eureka: false
    fetch-registry: false
    instance-info-replication-interval-seconds: 30
    serviceUrl:
      defaultZone: http://${spring.security.user.name}:${spring.security.user.password}@${eureka.instance.hostname}:${server.port}${server.servlet.context-path}/eureka/
      
```

再次重启Eureka，则需要登录

## 2. 认证服务中的Web安全配置和资源服务配置

web安全配置类： **==@EnableWebSecurity==**  // 开启了和Web相关的安全配置

用于==处理/oauth开头的请求==，Spring Cloud OAuth内部定义的获取令牌，

==刷新令牌的请求地址都是以/oauth/开头的==，也就是说IMoodSecurityConfig用于处理和令牌相关的请求；

因为当我们引入了spring-cloud-starter-oauth2依赖后，==系统会暴露一组由/oauth开头的端点==，这些端点用于处理令牌相关请求

这里可以详细看spring security的配置

```java
package com.dexlace.auth.config;

import com.dexlace.auth.filter.ValidateCodeFilter;
import com.dexlace.auth.service.IMoodUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.Resource;

/**
 * @Author: xiaogongbing
 * @Description:
 * @Date: 2021/6/30
 * 用于处理/oauth开头的请求，Spring Cloud OAuth内部定义的获取令牌，
 * 刷新令牌的请求地址都是以/oauth/开头的，也就是说IMoodSecurityConfigure用于处理和令牌相关的请求；
 * 因为当我们引入了spring-cloud-starter-oauth2依赖后，系统会暴露一组由/oauth开头的端点，这些端点用于处理令牌相关请求
 */
@Order(2)
@EnableWebSecurity  // 开启了和Web相关的安全配置
public class IMoodSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private IMoodUserDetailService userDetailService;

    /**
     * 加密的方法
     *
     * @return 加密编码器
     */
    @Resource
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ValidateCodeFilter validateCodeFilter;

    /**
     * 注册了一个authenticationManagerBean，因为密码模式需要使用到这个Bean
     *
     * @return
     * @throws Exception
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        /**
         * 该validateCodeFilter 需要在UsernamePasswordAuthenticationFilter之前
         */
        http.addFilterBefore(validateCodeFilter, UsernamePasswordAuthenticationFilter.class)
                .requestMatchers()
                .antMatchers("/oauth/**")  // 只对/oauth/开头的请求有效
                .and()
                .authorizeRequests()
                .antMatchers("/oauth/**").authenticated()
                .and()
                .csrf().disable();
    }

    /**
     * 最后我们重写了configure(AuthenticationManagerBuilder auth)方法，指定了userDetailsService和passwordEncoder。
     *
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailService).passwordEncoder(passwordEncoder);
    }

}

```

资源服务器配置类

继承了`ResourceServerConfigurerAdapter`，并重写了`configure(HttpSecurity http)`方法，通过`requestMatchers().antMatchers("/**")`的配置表明==该安全配置对所有请求都生效==。类上的`@EnableResourceServer`用于开启资源服务器相关配置

```java
package com.dexlace.auth.config;

import com.dexlace.auth.properties.IMoodAuthProperties;
import com.dexlace.common.handler.IMoodAccessDeniedHandler;
import com.dexlace.common.handler.IMoodAuthExceptionEntryPoint;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

import javax.annotation.Resource;

/**
 * @Author: xiaogongbing
 * @Description: 资源服务配置   注意  所以会有资源服务的错误处理的handler，自定义的
 * 资源服务器对所有请求都有效，但是其优先级是3，而WebSecurityConfigurerAdapter 的优先级是100，所以这个为优先过滤
 * 用于处理非/oauth/开头的请求，其主要用于资源的保护，客户端只能通过OAuth2协议发放的令牌来从资源服务器中获取受保护的资源。
 * @Date: 2021/6/30
 */
@Configuration
@EnableResourceServer // 用于开启资源服务器相关配置
public class IMoodResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Resource
    private IMoodAccessDeniedHandler accessDeniedHandler;

    @Resource
    private IMoodAuthExceptionEntryPoint exceptionEntryPoint;

    @Autowired
    private IMoodAuthProperties properties;


   @Override
    public void configure(HttpSecurity http) throws Exception {
        String[] anonUrls = StringUtils.splitByWholeSeparatorPreserveAllTokens(properties.getAnonUrl(), ",");
        http.csrf().disable()
                // 表明该安全配置对所有请求都生效
                .requestMatchers().antMatchers("/**")
                .and()
                .authorizeRequests()
                .antMatchers(anonUrls).permitAll()
                 .antMatchers("/actuator/**").permitAll()
                .antMatchers("/**").authenticated();
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.authenticationEntryPoint(exceptionEntryPoint)
                .accessDeniedHandler(accessDeniedHandler);
    }

}

```

<font color=red>`IMoodSecurityConfig`对`/oauth/`开头的请求生效，而`IMoodsourceServerConfig`对所有请求都生效</font>，那么当一个请求进来时，到底哪个安全配置先生效呢？其实并没有哪个配置先生效这么一说，当在Spring Security中定义了多个过滤器链的时候，==根据其优先级，只有优先级较高的过滤器链会先进行匹配。==

由于 **==@EnableWebSecurity==**  的==order为100==，==**@EnableResourceServer**==的==order为3==

显然，我们希望`/oauth/`开头的请求由`IMoodSecurityConfig`过滤器链处理，剩下的其他请求由`IMoodResourceServerConfig`过滤器链处理。

<font color=red>所以在`IMoodSecurityConfig`类上使用`Order(2)`注解标注即可</font>

## 3. 认证服务配置

```java
package com.dexlace.auth.config;

import com.dexlace.auth.properties.IMoodAuthProperties;
import com.dexlace.auth.properties.IMoodClientsProperties;
import com.dexlace.auth.service.IMoodUserDetailService;
import com.dexlace.auth.translator.IMoodWebResponseExceptionTranslator;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.builders.InMemoryClientDetailsServiceBuilder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

/**
 * @Author: xiaogongbing
 * @Description: 一个和认证服务器相关的安全配置类
 * 认证服务器相关配置
 * @Date: 2021/6/30
 */
@Configuration
@EnableAuthorizationServer
public class IMoodAuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private RedisConnectionFactory redisConnectionFactory;
    @Autowired
    private IMoodUserDetailService userDetailService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 要认证的属性，包括客户端，token的有效时间，refresh_token的有效时间
     */
    @Autowired
    private IMoodAuthProperties authProperties;

    /**
     * 异常翻译器的bean注入
     */
    @Autowired
    private IMoodWebResponseExceptionTranslator exceptionTranslator;

    /**
     * 这就是客户端配置
     *
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
//        clients.inMemory()
//                // 客户端从认证服务器获取令牌的时候，必须使用client_id为imood，client_secret为123456的标识来获取；
//                .withClient("imood")
//                .secret(passwordEncoder.encode("123456"))
//                // 该client_id支持password模式获取令牌，并且可以通过refresh_token来获取新的令牌
//                .authorizedGrantTypes("password", "refresh_token")
//                // 在获取client_id为febs的令牌的时候，scope只能指定为all，否则将获取失败；
//                .scopes("all");
//
//        // 如果需要指定多个client，可以继续使用withClient配置。

        IMoodClientsProperties[] authClients = authProperties.getClients();
        // 内存
        InMemoryClientDetailsServiceBuilder builder = clients.inMemory();
        if (ArrayUtils.isNotEmpty(authClients)) {
            for (IMoodClientsProperties client : authClients) {
                if (StringUtils.isBlank(client.getClient())) {
                    throw new Exception("client不能为空");
                }
                if (StringUtils.isBlank(client.getSecret())) {
                    throw new Exception("secret不能为空");
                }

                // 认证类型以逗号分割，得到认证类型的数组
                String[] grantTypes = StringUtils.splitByWholeSeparatorPreserveAllTokens(client.getGrantType(), ",");
                builder.withClient(client.getClient())
                        .secret(passwordEncoder.encode(client.getSecret()))
                        .authorizedGrantTypes(grantTypes)
                        .scopes(client.getScope());
            }


        }
    }

    /**
     * @param endpoints
     */
    @SuppressWarnings("all")
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints.tokenStore(tokenStore())
                .userDetailsService(userDetailService)
                .authenticationManager(authenticationManager)
                .tokenServices(defaultTokenServices())
                .exceptionTranslator(exceptionTranslator);
    }


    /**
     * tokenStore使用的是RedisTokenStore
     *
     * @return
     */
    @Bean
    public TokenStore tokenStore() {
        return new RedisTokenStore(redisConnectionFactory);
    }

    /**
     * 这是token配置
     *
     * @return
     */
    @Primary
    @Bean
    public DefaultTokenServices defaultTokenServices() {
        DefaultTokenServices tokenServices = new DefaultTokenServices();
        tokenServices.setTokenStore(tokenStore());
        // setSupportRefreshToken设置为true表示开启刷新令牌的支持。
        tokenServices.setSupportRefreshToken(true);
        // 令牌有效时间为60 * 60 * 24秒
//            tokenServices.setAccessTokenValiditySeconds(60 * 60 * 24);
        tokenServices.setAccessTokenValiditySeconds(authProperties.getAccessTokenValiditySeconds());
        // 刷新令牌有效时间为60 * 60 * 24 * 7秒
//            tokenServices.setRefreshTokenValiditySeconds(60 * 60 * 24 * 7);
        tokenServices.setRefreshTokenValiditySeconds(authProperties.getRefreshTokenValiditySeconds());
        return tokenServices;
    }
}

```

## 4.UserDetailService

用户信息加载服务，从哪里获得用户信息，如何生成UserDetail,==认证时是需要配置UserDetailService的==，如上一节

```java
package com.dexlace.auth.service;

import com.dexlace.auth.manager.UserManager;
import com.dexlace.common.entity.IMoodAuthUser;
import com.dexlace.common.entity.system.SystemUser;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @Author: xiaogongbing
 * @Description:
 * @Date: 2021/6/30
 */
@Service
public class IMoodUserDetailService  implements UserDetailsService {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserManager userManager;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        IMoodAuthUser user = new IMoodAuthUser();
//        user.setUsername(username);
//        user.setPassword(this.passwordEncoder.encode("123456"));
//
//        return new User(username, user.getPassword(), user.isEnabled(),
//                user.isAccountNonExpired(), user.isCredentialsNonExpired(),
//                user.isAccountNonLocked(), AuthorityUtils.commaSeparatedStringToAuthorityList("user:add"));

        SystemUser systemUser=userManager.findByName(username);
        if (systemUser != null) {
            // 找到该用户的权限
            String permissions = userManager.findUserPermissions(systemUser.getUsername());
            boolean notLocked = false;
            if (StringUtils.equals(SystemUser.STATUS_VALID, systemUser.getStatus()))
                notLocked = true;
            // 注意这里的password是加密后的
            IMoodAuthUser authUser = new IMoodAuthUser(systemUser.getUsername(), systemUser.getPassword(), true, true, true, notLocked,
                    AuthorityUtils.commaSeparatedStringToAuthorityList(permissions));
            BeanUtils.copyProperties(systemUser,authUser);
            return authUser;
        } else {
            throw new UsernameNotFoundException("");
        }


    }



}

```

## 5. 微服务网关

```java
package com.dexlace.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * @Author: xiaogongbing
 * @Description: 跨域处理
 * @Date: 2021/7/1
 *
 * 该配置类里注册了CorsFilter:
 *
 * setAllowCredentials(true)表示允许cookie跨域；
 * addAllowedHeader(CorsConfiguration.ALL)表示请求头部允许携带任何内容；
 * addAllowedOrigin(CorsConfiguration.ALL)表示允许任何来源；
 * addAllowedMethod(CorsConfiguration.ALL)表示允许任何HTTP方法。
 */
@Configuration
public class IMoodGatewayCorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.addAllowedHeader(CorsConfiguration.ALL);
        corsConfiguration.addAllowedOrigin(CorsConfiguration.ALL);
        corsConfiguration.addAllowedMethod(CorsConfiguration.ALL);
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(source);
    }
}

```

```java
package com.dexlace.gateway.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @Author: xiaogongbing
 * @Description: 因为imood-gateway引入了imood-common模块，
 * imood-common模块包含了Spring Cloud Security依赖，
 * 所以我们需要定义一个自己的WebSecurity配置类，来覆盖默认的。
 * 这里主要是关闭了csrf功能，否则会报csrf相关异常。
 * @Date: 2021/6/30
 */
@EnableWebSecurity
public class IMoodGatewaySecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests().antMatchers("/actuator/**").permitAll();
    }
}

```

## 6. 资源服务

### 6.1 资源服务配置

```java
package com.dexlace.system.config;

import com.dexlace.common.handler.IMoodAccessDeniedHandler;
import com.dexlace.common.handler.IMoodAuthExceptionEntryPoint;
import com.dexlace.system.properties.IMoodServerSystemProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

import javax.annotation.Resource;

/**
 * @Author: xiaogongbing
 * @Description: 所有访问imood-server-system的请求都需要认证，只有通过认证服务器发放的令牌才能进行访问
 * @Date: 2021/6/30
 */
@Configuration
@EnableResourceServer

public class IMoodServerSystemResourceServerConfig extends ResourceServerConfigurerAdapter {
    @Resource
    private IMoodAccessDeniedHandler accessDeniedHandler;

    @Resource
    private IMoodAuthExceptionEntryPoint exceptionEntryPoint;

    @Autowired
    private IMoodServerSystemProperties properties;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        String[] anonUrls = StringUtils.splitByWholeSeparatorPreserveAllTokens(properties.getAnonUrl(), ",");
        http.csrf().disable()
                // 表明该安全配置对所有请求都生效
                .requestMatchers().antMatchers("/**")
                .and()
                .authorizeRequests()
                .antMatchers(anonUrls).permitAll()
                .antMatchers("/**").authenticated();
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.authenticationEntryPoint(exceptionEntryPoint)
                .accessDeniedHandler(accessDeniedHandler);
    }
}

```

### 6.2 开启Security权限注解

`@EnableGlobalMethodSecurity(prePostEnabled = true)`,在启动类上配置，或者。。。见授权的方法授权

### 6.3 资源服务获取用户信息

```yml
security:
  oauth2:
    resource:
      id: ${spring.application.name}
      user-info-uri: http://localhost:8301/auth/user
```

该配置的作用主要有两个：

1. 到认证服务器里校验==当前请求头中的令牌是否为合法的令牌==；
2. 通过当前令牌==获取对应的用户信息==。

### 6.4 controller

```java
package com.dexlace.system.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * @Author: xiaogongbing
 * @Description:
 * @Date: 2021/6/30
 */
@RestController
public class TestController {
    @GetMapping("hello")
    public String hello(String name) {
        return "hello" + name;
    }

    @GetMapping("info")
    public String test(){
        return "imood-server-system";
    }

    @GetMapping("currentUser")
    public Principal currentUser(Principal principal) {
        return principal;
    }
}
```

## 7. 参数配置化

比如认证配置中的客户端信息

```java
@Data
public class IMoodClientsProperties {
    private String client;
    private String secret;
    private String grantType = "password,authorization_code,refresh_token";
    private String scope = "all";
}

```

```java
package com.dexlace.auth.properties;

import lombok.Data;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

/**
 * @Author: xiaogongbing
 * @Description: 认证属性，认证的客户端及access_token,refresh_token的有效时间
 * @Date: 2021/6/30
 */
@Data
@SpringBootConfiguration
@PropertySource(value = {"classpath:imood-auth.properties"})
@ConfigurationProperties(prefix = "imood.auth")
public class IMoodAuthProperties {
    // 认证多个服务器
    private IMoodClientsProperties[] clients = {};
    // accessTokenValiditySeconds用于指定access_token的有效时间
    private int accessTokenValiditySeconds = 60 * 60 * 24;
    // refreshTokenValiditySeconds用于指定refresh_token的有效时间
    private int refreshTokenValiditySeconds = 60 * 60 * 24 * 7;

    // 免认证路径
    private String anonUrl;

    // 验证码配置类
    private IMoodValidateCodeProperties code=new IMoodValidateCodeProperties();
}

```

```properties
imood.auth.clients[0].client=imood
imood.auth.clients[0].secret=123456
imood.auth.clients[0].grantType=password,authorization_code,refresh_token
imood.auth.clients[0].scope=all


imood.auth.clients[1].client=swagger
imood.auth.clients[1].secret=123456
imood.auth.clients[1].grantType=password
imood.auth.clients[1].scope=test

imood.auth.anonUrl=/captcha
```

应用在认证服务中的情况便如3所示

## 8.认证异常翻译

其实就是oauth2.0的认证异常

```java
package com.dexlace.auth.translator;

import com.dexlace.common.vo.IMoodResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.UnsupportedGrantTypeException;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.stereotype.Component;

/**
 * @Author: xiaogongbing
 * @Description: 认证异常翻译器
 * @Date: 2021/6/30
 */
@Slf4j
@Component
public class IMoodWebResponseExceptionTranslator implements WebResponseExceptionTranslator {


    @Override
    public ResponseEntity translate(Exception e) {
        // 500的错误
        ResponseEntity.BodyBuilder status = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR);
        IMoodResponse response = new IMoodResponse();
        String message = "认证失败";
        log.error(message, e);
        if (e instanceof UnsupportedGrantTypeException) {  // 都是oauth2.0定义的认证异常
            message = "不支持该认证类型";
            return status.body(response.message(message));
        }
        if (e instanceof InvalidGrantException) {
            if (StringUtils.containsIgnoreCase(e.getMessage(), "Invalid refresh token")) {
                message = "refresh token无效";
                return status.body(response.message(message));
            }
            if (StringUtils.containsIgnoreCase(e.getMessage(), "locked")) {
                message = "用户已被锁定，请联系管理员";
                return status.body(response.message(message));
            }
            message = "用户名或密码错误";
            return status.body(response.message(message));
        }
        return status.body(response.message(message));
    }
}

```

在认证服务中可以配置这个认证异常

## 9.401异常:令牌不正确

spring security的异常

```java
package com.dexlace.common.handler;

import com.alibaba.fastjson.JSONObject;
import com.dexlace.common.utils.IMoodUtil;
import com.dexlace.common.vo.IMoodResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author: xiaogongbing
 * @Description: 资源服务器异常处理 令牌不正确返回401
 * 实现了AuthenticationEntryPoint接口的commence方法，在方法内自定义了响应的格式。
 * @Date: 2021/7/1
 */
public class IMoodAuthExceptionEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        IMoodResponse imoodResponse = new IMoodResponse();

//        response.setContentType("application/json;charset=UTF-8");
//        response.setStatus(401);
//        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
//        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//        response.getOutputStream().write(JSONObject.toJSONString(imoodResponse.message("token无效")).getBytes());
        IMoodUtil.makeResponse(response,MediaType.APPLICATION_JSON_UTF8_VALUE,
                HttpServletResponse.SC_UNAUTHORIZED,imoodResponse.message("token无效"));
    }
}

```

## 10. 403异常：用户无权限

spring security的异常

```java
package com.dexlace.common.handler;

import com.dexlace.common.utils.IMoodUtil;
import com.dexlace.common.vo.IMoodResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author: xiaogongbing
 * @Description: 403 没有权限
 * @Date: 2021/7/1
 */
public class IMoodAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        IMoodResponse imoodResponse = new IMoodResponse();
        IMoodUtil.makeResponse(
                response, MediaType.APPLICATION_JSON_UTF8_VALUE,
                HttpServletResponse.SC_FORBIDDEN, imoodResponse.message("没有权限访问该资源"));
    }
}

```

## 11.如何使用自定义异常：自定义注解

==自定义注解+@Import==

注册之，但是，common模块中的定义的，common模块仅仅是作依赖定义而已，在各个模块通用，需要自定义注解

- 写一个配置类，把以上401和403异常注入

```java
package com.dexlace.common.config;

import com.dexlace.common.handler.IMoodAccessDeniedHandler;
import com.dexlace.common.handler.IMoodAuthExceptionEntryPoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * @Author: xiaogongbing
 * @Description: @ConditionalOnMissingBean注解的意思是 当IOC容器中没有指定名称或类型的Bean的时候，就注册它
 * 这样做的好处在于，子系统可以自定义自个儿的资源服务器异常处理器
 * @Date: 2021/7/1
 */
public class IMoodAuthExceptionConfig {
    @Bean
    @ConditionalOnMissingBean(name = "accessDeniedHandler")
    public IMoodAccessDeniedHandler accessDeniedHandler() {
        return new IMoodAccessDeniedHandler();
    }

    @Bean
    @ConditionalOnMissingBean(name = "authenticationEntryPoint")
    public IMoodAuthExceptionEntryPoint authenticationEntryPoint() {
        return new IMoodAuthExceptionEntryPoint();
    }
}

```

- ==自定义注解==

```java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(IMoodAuthExceptionConfig.class)  // 主要是这个注解注入了以上的两个组件
public @interface  EnableIMoodAuthExceptionHandler {
}

```

- 在各个模块的入口类上使用`@EnableIMoodAuthExceptionHandler`即可

- 然后可以在各个资源服务器中注入`IMoodAccessDeniedHandler`和`IMoodAuthExceptionEntryPoint`组件并配置之

## 12. Zuul异常

见`IMoodGatewayErrorFilter`

## 13.如何避免绕过网关一：自定义网关filter

在==网关转发请求前，请求头部加入网关信息==，然后在处理请求的==微服务模块里定义全局拦截器，校验请求头部的网关信息==，这样就能避免客户端直接访问微服务了。

zuul的过滤器不再细讲

```java
package com.dexlace.gateway.filter;

import com.dexlace.common.constant.IMoodConstant;
import com.dexlace.common.utils.IMoodUtil;
import com.dexlace.common.vo.IMoodResponse;
import com.dexlace.gateway.properties.IMoodGatewayProperties;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Base64Utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author: xiaogongbing
 * @Description: PreDecorationFilter用于处理请求上下文，优先级为5，
 * 所以我们可以定义一个优先级在PreDecorationFilter之后的过滤器，这样便可以拿到请求上下文。
 * @Date: 2021/7/1
 */
@Slf4j
@Component
public class IMoodGatewayRequestFilter  extends ZuulFilter {

    @Autowired
    private IMoodGatewayProperties properties;


    private AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 6;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        String serviceId = (String) ctx.get(FilterConstants.SERVICE_ID_KEY);
        HttpServletRequest request = ctx.getRequest();
        String host = request.getRemoteHost();
        String method = request.getMethod();
        String uri = request.getRequestURI();

        log.info("请求URI：{}，HTTP Method：{}，请求IP：{}，ServerId：{}", uri, method, host, serviceId);

        /*******************************************************************************************/
        /**********************************这里仅仅是另一个部分的功能而已：禁止特定的地址*****************/
        /*******************************************************************************************/
        // 禁止外部访问资源实现
        boolean shouldForward = true;
        // 获取外部资源访问的url
        String forbidRequestUri = properties.getForbidRequestUri();
        // 获取列表
        String[] forbidRequestUris = StringUtils.splitByWholeSeparatorPreserveAllTokens(forbidRequestUri, ",");
        if (forbidRequestUris != null && ArrayUtils.isNotEmpty(forbidRequestUris)) {
            for (String u : forbidRequestUris) {
                if (pathMatcher.match(u, uri)) {
                    shouldForward = false;
                }
            }
        }

        // 不允许外部访问
        if (!shouldForward) {
            HttpServletResponse response = ctx.getResponse();
            IMoodResponse febsResponse = new IMoodResponse().message("该URI不允许外部访问");
            try {

                IMoodUtil.makeResponse(
                        response, MediaType.APPLICATION_JSON_UTF8_VALUE,
                        HttpServletResponse.SC_FORBIDDEN, febsResponse
                );
                ctx.setSendZuulResponse(false);
                ctx.setResponse(response);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        /*******************************************************************************************/



        // 网关转发请求前，请求头部加入网关信息，定义了一个token

        byte[] token = Base64Utils.encode(IMoodConstant.ZUUL_TOKEN_VALUE.getBytes());
        ctx.addZuulRequestHeader(IMoodConstant.ZUUL_TOKEN_HEADER, new String(token));
        return null;
    }
}

```

## 14. 如何避免绕过网关二：HandlerInterceptor

HandlerInterceptor：SpringWebMVC的处理器拦截器，类似于Servlet开发中的过滤器Filter，用于处理器进行预处理和后处理。

### 14.1 应用场景

1. 日志记录，可以记录请求信息的日志，以便进行==信息监控、信息统计==等。
2. ==权限检查==：如登陆检测，进入处理器检测是否登陆，如果没有直接返回到登陆页面。
3. ==性能监控==：典型的是慢日志。

```java
public interface HandlerInterceptor {
 
   /**
     * 预处理回调方法，实现处理器的预处理（如检查登陆），第三个参数为响应的处理器，自定义Controller
     * 返回值：true表示继续流程（如调用下一个拦截器或处理器）；false表示流程中断
（如登录检查失败），不会继续调用其他的拦截器或处理器，此时我们需要通过response来产生响应；
   */
    boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception;
 
   /**
     * 后处理回调方法，实现处理器的后处理（但在渲染视图之前），此时我们可以
通过modelAndView（模型和视图对象）对模型数据进行处理或对视图进行处理，modelAndView也可能为null。
   */
    void postHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
            throws Exception;
 
   /**
    * 整个请求处理完毕回调方法，即在视图渲染完毕时回调，如性能监控中我们可以在此记录结束时间并输
出消耗时间，还可以进行一些资源清理，类似于try-catch-finally中的finally，但仅调用处理器执行链中
   */
    void afterCompletion(
            HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception;
 
 
}
```

- preHandle：==在业务处理器处理请求之前被调用==。预处理，可以进行==编码、安全控制、权限校验等处理==；
- postHandle：在业务处理器处理请求执行完成后，==生成视图之前执行==。后处理（调用了Service并返回ModelAndView，但未进行页面渲染），有机会修改ModelAndView （这个就基本不怎么用了）；
- afterCompletion：在DispatcherServlet完全处理完请求后被调用，==可用于清理资源等==。返回处理（已经渲染了页面）；

### 14.2 本例的HandlerInterceptor实现

```java
public class IMoodServerProtectorInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        // 从请求头中获取 Zuul Token
        String token = request.getHeader(IMoodConstant.ZUUL_TOKEN_HEADER);
        String zuulToken = new String(Base64Utils.encode(IMoodConstant.ZUUL_TOKEN_VALUE.getBytes()));
        // 校验 Zuul Token的正确性
        if (StringUtils.equals(zuulToken, token)) {
            return true;
        } else {
            IMoodResponse imoodResponse = new IMoodResponse();
            response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write(JSONObject.toJSONString(imoodResponse.message("请通过网关获取资源")));
            return false;
        }
    }

}

```

### 14.3 HandlerInterceptor注册到Spring

```JAVA
package com.dexlace.common.config;

import com.dexlace.common.interceptor.IMoodServerProtectorInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author: xiaogongbing
 * @Description:
 * 我们在该配置类里注册了ServerProtectInterceptor，并且将它加入到了Spring的拦截器链中。

 * 同样的，要让该配置类生效，我们可以定义一个@Enable注解来驱动它
 * @Date: 2021/7/1
 */
public class IMoodServerProtectConfig implements WebMvcConfigurer {
 

    @Bean
    public HandlerInterceptor imoodServerProtectInterceptor() {
        return new IMoodServerProtectorInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(imoodServerProtectInterceptor());
    }

}

```

### 14.4 自定义注解驱动之

```java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(IMoodServerProtectConfig.class)
public @interface EnableIMoodServerProtect {
}

```

在各个微服务入口类`@EnableIMoodServerProtect`

## 15. 服务调用的相关问题

- 如何保证携带令牌

- 如何保证只能通过网关调用

```java
package com.dexlace.common.config;

import com.dexlace.common.constant.IMoodConstant;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.util.Base64Utils;

/**
 * @Author: xiaogongbing
 * @Description:
 * @Date: 2021/7/1
 */
public class IMoodOAuth2FeignConfig {

    /**
     * 我们通过SecurityContextHolder从请求上下文中获取了OAuth2AuthenticationDetails类型对象，
     * 并通过该对象获取到了请求令牌，然后在请求模板对象requestTemplate的头部手动将令牌添加上去。
     * @return
     */

    @Bean
    public RequestInterceptor oauth2FeignRequestInterceptor() {
        return requestTemplate -> {
            // 添加 Zuul Token 解决防护的问题，必须通过网关访问
            String zuulToken = new String(Base64Utils.encode(IMoodConstant.ZUUL_TOKEN_VALUE.getBytes()));
            requestTemplate.header(IMoodConstant.ZUUL_TOKEN_HEADER, zuulToken);

            Object details = SecurityContextHolder.getContext().getAuthentication().getDetails();
            if (details instanceof OAuth2AuthenticationDetails) {
                // 在请求模板对象requestTemplate的头部手动将令牌添加上去。
                String authorizationToken = ((OAuth2AuthenticationDetails) details).getTokenValue();
                requestTemplate.header(HttpHeaders.AUTHORIZATION, "bearer " + authorizationToken);
            }
        };
    }
}

```

由于在调用者项目里开启了hystrix熔断机制，还需要添加如下配置

```yml
feign:
  hystrix:
    enabled: true
hystrix:
  shareSecurityContext: true  #把SecurityContext对象从你当前主线程传输到Hystrix线程
```

依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
</dependency>
```

如上述的系列注解定义

```java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(IMoodOAuth2FeignConfig.class)
public @interface EnableIMoodOauth2FeignClient {
}

```

同样在入口类中添加该注解

## 16. 跨域处理

```java
package com.dexlace.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * @Author: xiaogongbing
 * @Description: 跨域处理
 * @Date: 2021/7/1
 *
 * 该配置类里注册了CorsFilter:
 *
 * setAllowCredentials(true)表示允许cookie跨域；
 * addAllowedHeader(CorsConfiguration.ALL)表示请求头部允许携带任何内容；
 * addAllowedOrigin(CorsConfiguration.ALL)表示允许任何来源；
 * addAllowedMethod(CorsConfiguration.ALL)表示允许任何HTTP方法。
 */
@Configuration
public class IMoodGatewayCorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.addAllowedHeader(CorsConfiguration.ALL);
        corsConfiguration.addAllowedOrigin(CorsConfiguration.ALL);
        corsConfiguration.addAllowedMethod(CorsConfiguration.ALL);
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(source);
    }
}

```

## 17. 图形验证码集成

### 17.1 Redis相关依赖

common模块添加`spring-boot-starter-data-redis`依赖

使用Lettuce代替Jedis，==Lettuce基于Netty异步，拥有更好的性能==，引入如下：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-pool2</artifactId>
</dependency>
```

### 17.2 redis的相关实用配置

一个很好的redis各类操作方法的封装，在common包下

```java
public class RedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @return Boolean
     */
    public Boolean expire(String key, Long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据key获取过期时间
     *
     * @param key 键 不能为 null
     * @return 时间(秒) 返回 0代表为永久有效
     */
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 判断 key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public Boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     */
    public void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete(Arrays.asList(key));
            }
        }
    }

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    public Boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public Boolean set(String key, Object value, Long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 递增
     *
     * @param key   键
     * @param delta 要增加几(大于0)
     * @return Long
     */
    public Long incr(String key, Long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     *
     * @param key   键
     * @param delta 要减少几(小于0)
     * @return Long
     */
    public Long decr(String key, Long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, -delta);
    }

    /**
     * HashGet
     *
     * @param key  键 不能为 null
     * @param item 项 不能为 null
     * @return 值
     */
    public Object hget(String key, String item) {
        return redisTemplate.opsForHash().get(key, item);
    }

    /**
     * 获取 hashKey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<Object, Object> hmget(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * HashSet
     *
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public Boolean hmset(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * HashSet 并设置时间
     *
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public Boolean hmset(String key, Map<String, Object> map, Long time) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @return true 成功 false失败
     */
    public Boolean hset(String key, String item, Object value) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @param time  时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public Boolean hset(String key, String item, Object value, Long time) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除hash表中的值
     *
     * @param key  键 不能为 null
     * @param item 项 可以使多个不能为 null
     */
    public void hdel(String key, Object... item) {
        redisTemplate.opsForHash().delete(key, item);
    }

    /**
     * 判断hash表中是否有该项的值
     *
     * @param key  键 不能为 null
     * @param item 项 不能为 null
     * @return true 存在 false不存在
     */
    public Boolean hHasKey(String key, String item) {
        return redisTemplate.opsForHash().hasKey(key, item);
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key  键
     * @param item 项
     * @param by   要增加几(大于0)
     * @return Double
     */
    public Double hincr(String key, String item, Double by) {
        return redisTemplate.opsForHash().increment(key, item, by);
    }

    /**
     * hash递减
     *
     * @param key  键
     * @param item 项
     * @param by   要减少记(小于0)
     * @return Double
     */
    public Double hdecr(String key, String item, Double by) {
        return redisTemplate.opsForHash().increment(key, item, -by);
    }

    /**
     * 根据 key获取 Set中的所有值
     *
     * @param key 键
     * @return Set
     */
    public Set<Object> sGet(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key   键
     * @param value 值
     * @return true 存在 false不存在
     */
    public Boolean sHasKey(String key, Object value) {
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将数据放入set缓存
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public Long sSet(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    /**
     * 将set数据放入缓存
     *
     * @param key    键
     * @param time   时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public Long sSetAndTime(String key, Long time, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            if (time > 0)
                expire(key, time);
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    /**
     * 获取set缓存的长度
     *
     * @param key 键
     * @return Long
     */
    public Long sGetSetSize(String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    /**
     * 移除值为value的
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public Long setRemove(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().remove(key, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    /**
     * 获取list缓存的内容
     *
     * @param key   键
     * @param start 开始
     * @param end   结束 0 到 -1代表所有值
     * @return List
     */
    public List<Object> lGet(String key, Long start, Long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取list缓存的长度
     *
     * @param key 键
     * @return Long
     */
    public Long lGetListSize(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    /**
     * 通过索引 获取list中的值
     *
     * @param key   键
     * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；
     *              index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return Object
     */
    public Object lGetIndex(String key, Long index) {
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return Boolean
     */
    public Boolean lSet(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return Boolean
     */
    public Boolean lSet(String key, Object value, Long time) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            if (time > 0)
                expire(key, time);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return Boolean
     */
    public Boolean lSet(String key, List<Object> value) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return Boolean
     */
    public Boolean lSet(String key, List<Object> value, Long time) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0)
                expire(key, time);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据索引修改list中的某条数据
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return Boolean
     */
    public Boolean lUpdateIndex(String key, Long index, Object value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 移除N个值为value
     *
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public Long lRemove(String key, Long count, Object value) {
        try {
            return redisTemplate.opsForList().remove(key, count, value);
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }
}
```

==指定RedisTemplate的相关配置==

```java
package com.dexlace.common.config;

import com.dexlace.common.service.RedisService;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @Author: xiaogongbing
 * @Description:
 * @Date: 2021/7/1
 */
public class IMoodLettuceRedisConfig {
    // @ConditionalOnClass(RedisOperations.class)表示只有当项目里存在RedisOperations类的时候（即引入了spring-boot-starter-data-redis依赖的时候），我们自定义的RedisTemplateBean才会被注册到IOC容器中；
    @Bean
    @ConditionalOnClass(RedisOperations.class)
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        // 这个工厂在应用时会被yml配置而自动加载成一个RedisConnectionFactory
        template.setConnectionFactory(factory);


        // // 使用Jackson2JsonRedisSerialize 替换默认序列化(默认采用的是JDK序列化)
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(mapper);

        
        
        
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // key采用 String的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        // hash的 key也采用 String的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);
        // value序列化方式采用 jackson
        template.setValueSerializer(jackson2JsonRedisSerializer);
        // hash的 value序列化方式采用 jackson
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();

        return template;
    }


    // 将上面定义的RedisService注册到IOC容器中，前提是IOC容器里存在名称为redisTemplate的Bean
    @Bean
    @ConditionalOnBean(name = "redisTemplate")
    public RedisService redisService() {
        return new RedisService();
    }

}

```

### 17.3 自定义redis的注解

方便在各个模块中引入

```java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(IMoodLettuceRedisConfig.class)
public @interface EnableIMoodLettuceRedis {
}

```

在需要引入的模块的入口类中使用该注解即可，比如在auth模块中引用

同时注意配置redis对应的lettuce客户端

```yml
spring: 
    redis:
    database: 0
#    host: 192.168.205.114
    host: ${redis.url}
    port: 6379
    lettuce:
      pool:
        min-idle: 8
        max-idle: 500
        max-active: 2000
        max-wait: 10000
    timeout: 5000
    password: 123456
```

### 17.4 验证码

生成图形验证码可以借助GitHub的一个开源项目https://github.com/whvcse/EasyCaptcha，其提供了较为丰富的验证码配置可供选择。

```xml
<dependency>
    <groupId>com.github.whvcse</groupId>
    <artifactId>easy-captcha</artifactId>
    <version>1.6.2</version>
</dependency>
```

- ==验证码服务类==

```java
package com.dexlace.auth.service;

import com.dexlace.auth.properties.IMoodAuthProperties;
import com.dexlace.auth.properties.IMoodValidateCodeProperties;
import com.dexlace.common.constant.IMoodConstant;
import com.dexlace.common.exception.ValidateCodeException;
import com.dexlace.common.service.RedisService;
import com.wf.captcha.GifCaptcha;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author: xiaogongbing
 * @Description:
 * @Date: 2021/7/1
 */
@Service
public class ValidateCodeService {

    @Autowired
    private RedisService redisService;
    @Autowired
    private IMoodAuthProperties properties;

    /**
     * 生成验证码
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     */
    public void create(HttpServletRequest request, HttpServletResponse response) throws IOException, ValidateCodeException {
        String key = request.getParameter("key");
        if (StringUtils.isBlank(key)) {
            throw new ValidateCodeException("验证码key不能为空");
        }
        
        IMoodValidateCodeProperties code = properties.getCode();
        /**
         * setHeader用于设置响应头
         */
        setHeader(response, code.getType());

        Captcha captcha = createCaptcha(code);
        redisService.set(IMoodConstant.CODE_PREFIX + key, StringUtils.lowerCase(captcha.text()), code.getTime());
        captcha.out(response.getOutputStream());
    }

    /**
     * 校验验证码
     *
     * @param key   前端上送 key
     * @param value 前端上送待校验值
     */
    public void check(String key, String value) throws ValidateCodeException {
        Object codeInRedis = redisService.get(IMoodConstant.CODE_PREFIX + key);
        if (StringUtils.isBlank(value)) {
            throw new ValidateCodeException("请输入验证码");
        }
        if (codeInRedis == null) {
            throw new ValidateCodeException("验证码已过期");
        }
        if (!StringUtils.equalsIgnoreCase(value, String.valueOf(codeInRedis))) {
            throw new ValidateCodeException("验证码不正确");
        }
    }

    /**
     * createCaptcha方法通过验证码配置文件IMoodValidateCodeProperties生成相应的验证码
     * ，比如PNG格式的或者GIF格式的，验证码图片的长宽高，验证码字符的类型（纯数字，纯字母或者数字字母组合），验证 码字符的长度等
     * @param code
     * @return
     */
    private Captcha createCaptcha(IMoodValidateCodeProperties code) {
        Captcha captcha = null;
        if (StringUtils.equalsIgnoreCase(code.getType(), IMoodConstant.GIF)) {
            captcha = new GifCaptcha(code.getWidth(), code.getHeight(), code.getLength());
        } else {
            captcha = new SpecCaptcha(code.getWidth(), code.getHeight(), code.getLength());
        }
        captcha.setCharType(code.getCharType());
        return captcha;
    }

    // 在生成验证码图片后我们需要将其返回到客户端，
    // 所以需要根据不同的验证码格式设置不同的响应头；
    private void setHeader(HttpServletResponse response, String type) {
        if (StringUtils.equalsIgnoreCase(type, IMoodConstant.GIF)) {
            response.setContentType(MediaType.IMAGE_GIF_VALUE);
        } else {
            response.setContentType(MediaType.IMAGE_PNG_VALUE);
        }
        response.setHeader(HttpHeaders.PRAGMA, "No-cache");
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache");
        response.setDateHeader(HttpHeaders.EXPIRES, 0L);
    }
}

```

```java
  /**
     * 因为我们验证码是供客户端认证的时候使用的，这时候客户端还没有获取到令牌，所以我们的验证码生成服务需要配置为免认证
     * @param request
     * @param response
     * @throws IOException
     * @throws ValidateCodeException
     */
    @GetMapping("captcha")
    public void captcha(HttpServletRequest request, HttpServletResponse response) throws IOException, ValidateCodeException {
        validateCodeService.create(request, response);
    }
```

注意以上的properties类见原项目

注意，这时候客户端还没有获取到令牌，所以我们的==验证码生成服务需要配置为免认证==，至于怎么免认证，路径怎么配置，不必再提

- ==验证码过滤器==

```java
package com.dexlace.auth.filter;

import com.dexlace.auth.service.ValidateCodeService;
import com.dexlace.common.exception.ValidateCodeException;
import com.dexlace.common.utils.IMoodUtil;
import com.dexlace.common.vo.IMoodResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @Author: xiaogongbing
 * @Description: 该filter顾名思义只可以执行一次  需要在UsernamePasswordAuthenticationFilter之前
 * @Date: 2021/7/1
 */
@Slf4j
@Component
public class ValidateCodeFilter extends OncePerRequestFilter {
    /**
     * 当拦截的请求URI为/oauth/token，请求方法为POST并且请求参数grant_type为password的时候（对应密码模式获取令牌请求），需要进行验证码校验
     */
    @Autowired
    private ValidateCodeService validateCodeService;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String header = httpServletRequest.getHeader("Authorization");
        String clientId = getClientId(header, httpServletRequest);

        RequestMatcher matcher = new AntPathRequestMatcher("/oauth/token", HttpMethod.POST.toString());
        if (matcher.matches(httpServletRequest)
                && StringUtils.equalsIgnoreCase(httpServletRequest.getParameter("grant_type"), "password")
                // 获取了ClientId后，我们判断ClientId是否为swagger，是的话无需进行图形验证码校验。
                && !StringUtils.equalsIgnoreCase(clientId, "swagger")) {
            try {
                validateCode(httpServletRequest);
                filterChain.doFilter(httpServletRequest, httpServletResponse);
            } catch (ValidateCodeException e) {
                IMoodResponse imoodResponse = new IMoodResponse();
                IMoodUtil.makeResponse(httpServletResponse, MediaType.APPLICATION_JSON_UTF8_VALUE,
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR, imoodResponse.message(e.getMessage()));
                log.error(e.getMessage(), e);
            }
        } else {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }
    }

    /**
     * 验证码校验
     * @param httpServletRequest
     * @throws ValidateCodeException
     */
    private void validateCode(HttpServletRequest httpServletRequest) throws ValidateCodeException {
        String code = httpServletRequest.getParameter("code");
        String key = httpServletRequest.getParameter("key");
        validateCodeService.check(key, code);
    }


    /**
     * getClientId这个方法用于从请求头部获取ClientId信息，这段代码是从Spring Cloud OAuth2源码中拷贝过来的，
     * 所以看不懂没关系，只要知道它的作用就行了。
     * @param header
     * @param request
     * @return
     */
    private String getClientId(String header, HttpServletRequest request) {
        String clientId = "";
        try {
            byte[] base64Token = header.substring(6).getBytes(StandardCharsets.UTF_8);
            byte[] decoded;
            decoded = Base64.getDecoder().decode(base64Token);

            String token = new String(decoded, StandardCharsets.UTF_8);
            int delim = token.indexOf(":");
            if (delim != -1) {
                clientId = new String[]{token.substring(0, delim), token.substring(delim + 1)}[0];
            }
        } catch (Exception ignore) {
        }
        return clientId;
    }
}

```

注意`ValidateCodeFilter`需要在`UsernamePasswordAuthenticationFilter`之前就执行,在auth项目下的资源配置下

### 17.5 验证码限流

```xml
<dependency>
    <groupId>com.alibaba.csp</groupId>
    <artifactId>sentinel-zuul-adapter</artifactId>
    <version>1.6.3</version>
</dependency>
```

```java
package com.dexlace.gateway.filter;

import com.alibaba.csp.sentinel.adapter.gateway.common.SentinelGatewayConstants;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiDefinition;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPathPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.GatewayApiDefinitionManager;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayParamFlowItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.csp.sentinel.adapter.gateway.zuul.fallback.ZuulBlockFallbackManager;
import com.alibaba.csp.sentinel.adapter.gateway.zuul.filters.SentinelZuulErrorFilter;
import com.alibaba.csp.sentinel.adapter.gateway.zuul.filters.SentinelZuulPostFilter;
import com.alibaba.csp.sentinel.adapter.gateway.zuul.filters.SentinelZuulPreFilter;
import com.dexlace.gateway.fallback.IMoodGatewayBlockFallbackProvider;
import com.netflix.zuul.ZuulFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author: xiaogongbing
 * @Description:
 * @Date: 2021/7/1
 */
@Slf4j
@Configuration
public class IMoodGatewaySentinelFilter {
    @Bean
    public ZuulFilter sentinelZuulPreFilter() {
        return new SentinelZuulPreFilter();
    }

    @Bean
    public ZuulFilter sentinelZuulPostFilter() {
        return new SentinelZuulPostFilter();
    }

    @Bean
    public ZuulFilter sentinelZuulErrorFilter() {
        return new SentinelZuulErrorFilter();
    }

    @PostConstruct
    public void doInit() {
        ZuulBlockFallbackManager.registerProvider(new IMoodGatewayBlockFallbackProvider());
        initGatewayRules();
    }

    /**
     * 定义验证码请求限流，限流规则：
     *  60秒内同一个IP，同一个 key最多访问 10次
     */
    private void initGatewayRules() {
        Set<ApiDefinition> definitions = new HashSet<>();
        Set<ApiPredicateItem> predicateItems = new HashSet<>();

        // 验证码服务是在auth项目下，网关路径加/auth/captcha
        predicateItems.add(new ApiPathPredicateItem().setPattern("/auth/captcha"));
        // 用户自定义的 API 定义分组，可以看做是一些 URL 匹配的组合。和swagger的使用有点类似
        // 定义了一个名称为captcha的API分组
        // 匹配的URL是/auth/captcha
        ApiDefinition definition = new ApiDefinition("captcha")
                .setPredicateItems(predicateItems);
        definitions.add(definition);

        
        // 一个manager加载了该分组
        GatewayApiDefinitionManager.loadApiDefinitions(definitions);

        // 限流规则
        Set<GatewayFlowRule> rules = new HashSet<>();

        rules.add(new GatewayFlowRule("captcha")  // 资源名称，可以是网关中的 route 名称或者用户自定义的 API 分组名称。
                .setResourceMode(SentinelGatewayConstants.RESOURCE_MODE_CUSTOM_API_NAME)
                                   //规则是针对 API Gateway 的 route（RESOURCE_MODE_ROUTE_ID）
                                   // 还是用户在 Sentinel 中定义的 API 分组（RESOURCE_MODE_CUSTOM_API_NAME），默认是 route。
                //这里是api分组
                .setParamItem(
                        new GatewayParamFlowItem()
                                .setParseStrategy(SentinelGatewayConstants.PARAM_PARSE_STRATEGY_URL_PARAM)
                                .setFieldName("key")
                                .setMatchStrategy(SentinelGatewayConstants.PARAM_MATCH_STRATEGY_EXACT)
                                .setParseStrategy(SentinelGatewayConstants.PARAM_PARSE_STRATEGY_CLIENT_IP)
                )
                .setCount(10)
                .setIntervalSec(60)
        );
        GatewayRuleManager.loadRules(rules);
    }
}

```

## 18. 整合Swagger

略，但是注意需要为swagger配置一个客户端，专门用于Swagger令牌发放，详见项目

## 19. 整合Spring Boot Admin

### 19.1 服务端

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>imood-monitor</artifactId>
        <groupId>com.dexlace</groupId>
        <version>1.0-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>imood-monitor-admin</artifactId>
    <name>IMOOD-Monitor-Admin</name>
    <description>IMOOD-Monitor-Admin基于Spring Boot Admin搭建的监控程序</description>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>

        </dependency>

        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>de.codecentric</groupId>
            <artifactId>spring-boot-admin-server</artifactId>
            <version>2.1.6</version>
        </dependency>
        <dependency>
            <groupId>de.codecentric</groupId>
            <artifactId>spring-boot-admin-server-ui</artifactId>
            <version>2.1.6</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
```

```java
@EnableAdminServer
@SpringBootApplication
public class IMoodMonitorAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(IMoodMonitorAdminApplication.class, args);
    }
}

```

```yml
server:
  port: 8401

spring:
  application:
    name: IMOOD-Monitor-Admin
  boot:
    admin:
      ui:
        title: ${spring.application.name}


  security:
    user:
      name: imood
      password: 123456   # 因为imood-monitor-admin模块后续需要收集各个微服务的信息，这些信息较为敏感，我们一般不希望这些信息被无关人士查阅，所以我们可以通过整合Spring Cloud Security来保护imood-monitor-admin
```



```java
package com.dexlace.monitor.config;

import de.codecentric.boot.admin.server.config.AdminServerProperties;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

/**
 * @Author: xiaogongbing
 * @Description:
 * @Date: 2021/7/4
 */
@EnableWebSecurity
public class IMoodSecurityConfig extends WebSecurityConfigurerAdapter {
    private final String adminContextPath;

    public IMoodSecurityConfig(AdminServerProperties adminServerProperties) {
        this.adminContextPath = adminServerProperties.getContextPath();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
        successHandler.setTargetUrlParameter("redirectTo");

        http.authorizeRequests()
                // 配置了免认证路径，比如/assets/**静态资源和/login登录页面
                .antMatchers(adminContextPath + "/assets/**").permitAll()
                .antMatchers(adminContextPath + "/login").permitAll()
                .anyRequest().authenticated()
                .and()
                // 配置了登录页面为/login，登出页面为/logout。
                .formLogin().loginPage(adminContextPath + "/login").successHandler(successHandler).and()
                .logout().logoutUrl(adminContextPath + "/logout").and()
                .httpBasic().and()
                .csrf().disable();
    }
}

```

<img src="Spring-Security%E5%BE%AE%E6%9C%8D%E5%8A%A1%E8%A7%A3%E5%86%B3%E6%96%B9%E6%A1%88.assets/image-20210707220129233.png" alt="image-20210707220129233" style="zoom:80%;" />

### 19.2 客户端

略，具体看项目实现吧

# 分割线

## 3. 关于认证服务器的安全配置详解

###  3.1 概念简介

把spring security+oauth2.0过一遍

**认证** ：==<font color=red>用户认证就是判断一个用户的身份是否合法的过程</font>==，用户去访问系统资源时系统要求验证用户的身份信 

息，身份合法方可继续访问，不合法则拒绝访问。常见的用户身份认证方式有：用户名密码登录，二维码登录，手 

机短信登录，指纹认证等方式。 

**会话** ：用户认证通过后，为了避免用户的每次操作都进行认证可将用户的信息保证在会话中。==会话就是系统为了保持当前== 

==用户的登录状态所提供的机制，常见的有基于session方式、基于token方式等==

**基于session的认证方式**：它的交互流程是，用户认证成功后，在服务端生成用户相关的数据保存在session(当前会话)中，发给

客户端的 sesssion_id 存放到 cookie 中，这样用户客户端请求时带上 session_id 就可以验证服务器端是否存在 session 数 

据，以此完成用户的合法校验，当用户退出系统或session过期销毁时,客户端的session_id也就无效了。

**基于token的认证方式**它的交互流程是，用户认证成功后，服务端生成一个token发给客户端，客户端可以放到 cookie 或 

localStorage 等存储中，每次请求时带上 token，**服务端收到token通过验证**后即可确认用户身份。 

==<font color=red>基于session的认证方式由Servlet规范定制，服务端要存储session信息需要占用内存资源，客户端需要支持 </font>==

==<font color=red>cookie</font>==；基于token的方式则==**一般不需要服务端存储token**==，并且不限制客户端的存储方式。如今移动互联网时代 

更多类型的客户端需要接入系统，系统多是采用前后端分离的架构进行实现，所以基于token的方式更适合。

**授权**： 授权是用户认证通过根据用户的权限来控制用户访问资源的过程，拥有资源的访问权限则正常访问，没有 

权限则拒绝访问。 是为了==<font color=red>更细粒度的对隐私数据进行划分</font>==

==如果你不会基于session的认证和授权，你可以去死==

### 3.2 授权的数据模型

用户、角色、权限、资源四个层级

一般把权限和资源合并为一张表，所以数据模型为

<img src="C:%5CUsers%5CAdministrator%5CAppData%5CRoaming%5CTypora%5Ctypora-user-images%5Cimage-20210706100519635.png" alt="image-20210706100519635" style="zoom:80%;" />

==一共五张表==

RBAC:==Role-Based Access Control==，按照角色授权；或者==Resource-Based Access Control==，按照资源授权

很显然，前者粒度大，后者粒度小，怎么选择看你自己

### 3.3 Spring Security

基础入门可以看spring security与springboot集成的例子

是一系列的拦截器构成

#### 1. 基本配置

spring security提供了==用户名密码登录、退出、会话管理等认证功能，只需要配置即可使用==。

继承WebSecurityConfigurerAdapter，如下

```java
package com.doglast.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * @author Administrator
 * @version 1.0
 **/
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

   //定义用户信息服务（查询用户信息）Spring Security会使用它来 获取用户信息
   // 我们暂时使用InMemoryUserDetailsManager实现类，并在其中分别创建了zhangsan、lisi两个用户，并设置密码和权限
   // 用户信息加载方式  关键字
    // 信息加载  信息加载  信息加载  信息加载  信息加载
    // 信息加载  信息加载  信息加载  信息加载  信息加载 
    // 信息加载  信息加载  信息加载  信息加载  信息加载
    @Bean
    public UserDetailsService userDetailsService(){
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("zhangsan").password("123").authorities("p1").build());
        manager.createUser(User.withUsername("lisi").password("456").authorities("p2").build());
        return manager;
    }
    // 我们可以额外实现UserDetailsService
    // 见下一节自定义实现： SpringDataUserDetailsService
    /*********************************************************************************************/     

    //密码编码器
    @Bean
    public PasswordEncoder passwordEncoder(){
        return NoOpPasswordEncoder.getInstance();
    }

    
    
    
    
    
    //安全拦截机制（最重要） 授权
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // /r/r1之类的路径对应对应的controller
        http.authorizeRequests()
                .antMatchers("/r/r1").hasAuthority("p1")
                .antMatchers("/r/r2").hasAuthority("p2")
                .antMatchers("/r/**").authenticated()//所有/r/**的请求必须认证通过
                .anyRequest().permitAll()//除了/r/**，其它的请求可以访问 也就是放开了其他访问请求
                .and()
                .formLogin()//允许表单登录
                .successForwardUrl("/login-success");//自定义登录成功的页面地址

    }
}

```

#### 2. 认证

<img src="C:%5CUsers%5CAdministrator%5CAppData%5CRoaming%5CTypora%5Ctypora-user-images%5Cimage-20210706104310217.png" alt="image-20210706104310217" style="zoom:80%;" />

具体不怎么解释，知道**关键信息**==认证管理器（Authentication Manager）==<font color=red>委托</font>==认证服务提供者（Authentication Provider）==进行实现认证流程就可

- <font color=red>**AuthenticationProvider:完成认证流程**</font>

```java
// 不同的认证方式有不同的以下接口实现类
public interface AuthenticationProvider { 
    // 实现认证流程
    // Authentication authentication 包含用户提交的用户名和密码
    // 返回也是一个 Authentication，不过此时已经组装了其他信息，主要是权限和用户自定义信息
    Authentication authenticate(Authentication authentication) throws AuthenticationException; boolean supports(Class<?> var1); 
}

// 每个AuthenticationProvider接口都必须实现support()方法来表明自己支持的认证方式
// 如我们使用表单方式认证， 在提交请求时Spring Security会生成UsernamePasswordAuthenticationToken，它是一个Authentication 使用DaoAuthenticationProvider来处理，其基类AbstractUserDetailsAuthenticationProvider中
public boolean supports(Class<?> authentication) {
    return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
}
// 也就是说当web表单提交用户名密码时，Spring Security由DaoAuthenticationProvider处理
```

- <font color=red>**Authentication**：表示着一个抽象主体身份</font>

```java
public interface Authentication extends Principal, Serializable {
    // 权限信息列表，默认是GrantedAuthority接口的一些实现类，通常是代表权限信息的一系列字符串
    Collection<? extends GrantedAuthority> getAuthorities();  
    // 凭证信息，用户输入的密码字符串，在认证过后通常会被移除，用于保障安全
    Object getCredentials();
    // 细节信息，web应用中的实现接口通常为 WebAuthenticationDetails，它记录了访问者的ip地 址和sessionId的值。
    Object getDetails();  
    
    Object getPrincipal();  
    boolean isAuthenticated(); 
    void setAuthenticated(boolean var1) throws IllegalArgumentException; 
}
```

<font color=red>getPrincipal():</font>，身份信息，大部分情况下返回的是==UserDetails接口的实现类==，UserDetails代表用户的详细信息，那从Authentication中取出来的UserDetails就是当前登录用户信息，它也是框架中的常用接口之一。

- <font color=red>**UserDetailService**</font>:其实UserDetailsService**<font color=red>只负责从特定的地方（通常是数据库）加载用户信息</font>**

认证成功后既得到一个 Authentication(UsernamePasswordAuthenticationToken实现)，里面包含了身份信息（Principal）。这个身份 信息就是一个 Object ，==大多数情况下它可以被强转为UserDetails对象==。 

<font color=red>**比如DaoAuthenticationProvider**中包含了一个UserDetailsService实例</font>，它负责根据用户名提取用户信息 UserDetails(包含密码)，而后DaoAuthenticationProvider**会去对比UserDetailsService提取的用户密码与用户提交的密码**是否匹配作为认证成功的关键依据

```java
public interface UserDetailsService { 
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException; 
}
```

```java
// WebSecurityConfig 中则不必配置UserDetailService了
@Service
public class SpringDataUserDetailsService implements UserDetailsService {

    @Autowired
    UserDao userDao;

    //根据 账号查询用户信息
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //将来连接数据库根据账号查询用户信息
        UserDto userDto = userDao.getUserByUsername(username);
        if(userDto == null){
            //如果用户查不到，返回null，由provider来抛出异常
            return null;
        }
        //根据用户的id查询用户的权限
        List<String> permissions = userDao.findPermissionsByUserId(userDto.getId());
        //将permissions转成数组
        String[] permissionArray = new String[permissions.size()];
        permissions.toArray(permissionArray);
        UserDetails userDetails = User.withUsername(userDto.getUsername()).password(userDto.getPassword()).authorities(permissionArray).build();
        return userDetails;
    }
}

```



- <font color=red>**UserDetails**</font>：**用户信息**

```java
public interface UserDetails extends Serializable { 
    Collection<? extends GrantedAuthority> getAuthorities(); 
    String getPassword(); 
    
    String getUsername(); 
    
    boolean isAccountNonExpired(); 
    
    boolean isAccountNonLocked(); 
    
    boolean isCredentialsNonExpired(); 
    
    boolean isEnabled(); 
}
```

**==通过实现<font color=red>UserDetailsService和UserDetails</font>，我们可以完成对<font color=red>用户信息获取方式</font>以及<font color=red>用户信息字段的扩展</font>。==**

- **<font color=red>passwordEncoder</font>**

```java
public interface PasswordEncoder { 
    String encode(CharSequence var1); 
    boolean matches(CharSequence var1, String var2); 
    default boolean upgradeEncoding(String encodedPassword) {
        return false;
    } 
}
```

在这里Spring Security为了适应多种多样的加密类型,开箱即用

==如`NoOpPasswordEncoder`和`BCryptPasswordEncoder `==

注意使用`BCryptPasswordEncoder `时，==创建UserDetail时需要将原始密码改为BCrypt格式==

#### 3. 授权

授权的方式包括 ==web授权==和==方法授权==，web授权是==通过 url拦截进行授权==，方法授权是==通过方法拦截进行授权==。他们都会调用accessDecisionManager进行授权决策，若为web授权则拦截器为FilterSecurityInterceptor；若为方法授权则拦截器为MethodSecurityInterceptor。==<font color=red>如果同时通过web授权和方法授权则先执行web授权，再执行方法授权</font>==，最后决策通过，则允许访问资源，否则将禁止访问。 Spring Security可以通过` http.authorizeRequests() `对web请求进行授权保护

- <font color=red>**web授权**</font>

通过给 ==http.authorizeRequests() 添加多个子节点来定制需求到我们的URL==，如下代码：

```java
@Override 
protected void configure(HttpSecurity http) throws Exception {
    http .authorizeRequests() //  http.authorizeRequests() 方法有多个子节点，每个macher按照他们的声明顺序执行。
        .antMatchers("/r/r1").hasAuthority("p1") 
        .antMatchers("/r/r2").hasAuthority("p2") 
         // 指定了"/r/r3"URL，同时拥有p1和p2权限才能够访问
        .antMatchers("/r/r3").access("hasAuthority('p1') and hasAuthority('p2')") 
       
        // 指定了除了r1、r2、r3之外"/r/**"资源，同时通过身份认证就能够访问
        .antMatchers("/r/**").authenticated()  
        // 剩余的尚未匹配的资源不做保护
        .anyRequest().permitAll()  
        .and() .formLogin() // ... 
}
// 规则的顺序是重要的,更具体的规则应该先写.
```

<img src="C:%5CUsers%5CAdministrator%5CAppData%5CRoaming%5CTypora%5Ctypora-user-images%5Cimage-20210706230408299.png" alt="image-20210706230408299" style="zoom:80%;" />

具体流程略

- **==<font color=red>方法授权</font>==**

在配置 类上加上注解`@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)`表示开启方法授权，然后在controller上使用==`@PreAuthorize,@PostAuthorize, @Secured`三类注解即可==

==见会话中的示例==

#### 4. 表单登录

```java
//配置安全拦截机制 
@Override protected void configure(HttpSecurity http) throws Exception { 
    http.csrf().disable() // spring security为防止CSRF（Cross-site request forgery跨站请求伪造）的发生，限制了除了get以外的大多数方 法。屏蔽CSRF控制，即spring security不再限制CSRF。
        .authorizeRequests() 
        .antMatchers("/r/**").authenticated() 
        .anyRequest().permitAll() 
        .and() 
        .formLogin() // 允许表单登录
        .loginPage("/login‐view")  // 指定跳转页面
        .loginProcessingUrl("/login")   // 登录处理的URL  ，也就是用户名、密码表单提交的目的路径
        .successForwardUrl("/login‐success")   // 指定登录成功后的跳转URL
        
        .permitAll(); //我们必须允许所有用户访问我们的登录页（例如为验证的用户），这个 formLogin().permitAll() 方法允许 任意用户访问基于表单登录的所有的URL。
}
```

#### 5. 会话

用户认证通过后，为了避免用户的每次操作都进行认证可将用户的信息保存在会话中。

spring security提供会话管理，认证通过后==将身份信息放入SecurityContextHolder上下文==，SecurityContext与当前线程进行绑

定，方便获取用户身份。 

##### 1）获取用户身份

```java
/**
 * @author Administrator
 * @version 1.0
 **/
@RestController
public class LoginController {

    @RequestMapping(value = "/login-success",produces = {"text/plain;charset=UTF-8"})
    public String loginSuccess(){
        //提示具体用户名称登录成功
        return getUsername()+" 登录成功";
    }

    /**
     * 测试资源1
     * @return
     */
    @GetMapping(value = "/r/r1",produces = {"text/plain;charset=UTF-8"})
    @PreAuthorize("hasAuthority('p1')")//拥有p1权限才可以访问
    public String r1(){
        return getUsername()+" 访问资源1";
    }

    /**
     * 测试资源2
     * @return
     */
    @GetMapping(value = "/r/r2",produces = {"text/plain;charset=UTF-8"})
    @PreAuthorize("hasAuthority('p2')")//拥有p2权限才可以访问
    public String r2(){
        return getUsername()+" 访问资源2";
    }

    //获取当前用户信息
    private String getUsername(){
        String username = null;
        //当前认证通过的用户身份
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //用户身份
        Object principal = authentication.getPrincipal();
        if(principal == null){
            username = "匿名";
        }
        // 前面userDetail和principal的关系在此可以看见
        if(principal instanceof UserDetails){
            UserDetails userDetails = (UserDetails) principal;
            username = userDetails.getUsername();
        }else{
            username = principal.toString();
        }
        return username;
    }
}

```

##### 2）会话控制

<img src="C:%5CUsers%5CAdministrator%5CAppData%5CRoaming%5CTypora%5Ctypora-user-images%5Cimage-20210706155454988.png" alt="image-20210706155454988" style="zoom:80%;" />

<img src="C:%5CUsers%5CAdministrator%5CAppData%5CRoaming%5CTypora%5Ctypora-user-images%5Cimage-20210706155509225.png" alt="image-20210706155509225" style="zoom:80%;" />

<img src="C:%5CUsers%5CAdministrator%5CAppData%5CRoaming%5CTypora%5Ctypora-user-images%5Cimage-20210706155538765.png" alt="image-20210706155538765" style="zoom:80%;" />

<img src="C:%5CUsers%5CAdministrator%5CAppData%5CRoaming%5CTypora%5Ctypora-user-images%5Cimage-20210706155552083.png" alt="image-20210706155552083" style="zoom:80%;" />

#### 6. 退出

```java
  //安全拦截机制（最重要）
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 注意：如果让logout在GET请求下生效，必须关闭防止CSRF攻击csrf().disable()。如果开启了CSRF，必须使用 post方式请求/logout
        http.csrf().disable()
                .authorizeRequests()
//...
                .and()
                .logout()
                .logoutUrl("/logout")  // 设置触发退出操作的URL (默认是 /logout )
                .logoutSuccessUrl("/login-view?logout")
                .logoutSuccessHandler(logoutSuccessHandler) // 定制的 LogoutSuccessHandler ，用于实现用户退出成功时的处理。如果指定了这个选项那么 logoutSuccessUrl() 的设置会被忽略。
                .addLogoutHandler(logoutHandler) // 用于实现用户退出时的清理工作.默认 SecurityContextLogoutHandler 会被添加 为最后一个 LogoutHandler 。
                .invalidateHttpSession(true);// 指定是否在退出时让 HttpSession 无效。 默认设置为 true。

    }
```

## 4. 关于分布式系统认证方案

分布式系统的每个服务都会有认证、授权的需求，如果每个服务都实现一套认证授权逻辑会非常冗余，考虑分布式系统共享性的特点，需要由==独立的认证服务处理系统认证授权的请求==；考虑分布式系统开放性的特点，不仅对系统内部服务提供认证，对第三方系统也要提供认证。分布式认证的需求总结如下： 

**统一认证授权** 

提供独立的认证服务，==统一处理认证授权==

**应用接入认证** 

应提供扩展和开放能力，==提供安全的系统对接机制==，并可开放部分API给接入第三方使用，一方应用（内部 系统服 

务）和三方应用（第三方应用）均采用统一机制接入。 

==基于session和基于token的认证方案的选型详见spring security笔记==

<img src="C:%5CUsers%5CAdministrator%5CAppData%5CRoaming%5CTypora%5Ctypora-user-images%5Cimage-20210706232541736.png" alt="image-20210706232541736" style="zoom:80%;" />

#### 4.1 OAuth2.0简介

OAuth（开放授权）是一个开放标准，<font color=red>**允许用户授权第三方应用访问他们存储在另外的服务提供者上的信息**</font>，而不 需要将用户名和密码提供给第三方应用或分享他们数据的所有内容。OAuth2.0是OAuth协议的延续版本，但不向 后兼容OAuth 1.0即完全废止了OAuth1.0。很多大公司如Google，Yahoo，Microsoft等都提供了OAUTH认证服 务，这些都足以说明OAUTH标准逐渐成为开放资源授权的标准。 

OAauth2.0包括以下角色： 

- 客户端
- 资源拥有者：通常为用户，拥有资源访问权限的用户，也可以是应用程序
- 认证服务器：授权并发放令牌
- 资源服务器：存储资源的服务器，下面例子为==微信存储的用户信息==

以牛客网允许微信登录的过程为例

- 用户是==资源拥有者==，自己的用户信息即资源
- 用户扫描二维码，并同意授权（==即让牛客网访问自己的微信数据==），微信会==颁发一个<font color=red>**授权码**</font>到牛客网（客户端）==
- 牛客网==客户端==向==微信认证服务器==申请==令牌==
- 微信认证服务器响应令牌
- 客户端依据令牌有了一定的通信证
- 客户端能够访问一定的资源

#### 4.2 OAuth2.0的两个服务

- **授权服务**
- - 包含对接入端以及登入用户的合法性进行验证并颁发token等功能
  - **AuthorizationEndpoint** 服务于认证请求。==默认 URL： /oauth/authorize==
  - **TokenEndpoint** 服务于==访问令牌的请求==。==默认 URL： /oauth/token ==

- **资源服务**
- - 包含对==资源的保护功能，对非法请求进行拦截==，对请求中token进行解析鉴权等

#### 4.3 授权服务器的配置

==可以用` @EnableAuthorizationServer` 注解并继承`AuthorizationServerConfifigurerAdapter`==来配置OAuth2.0 授权服务器。 

##### 1. 配置客户端详细信息

```java
  /*****************************************************************************************/
    /*****************************************************************************************/
    // 第一
    //ClientDetailsServiceConfigurer：用来配置客户端详情服务（ClientDetailsService），
    // 客户端详情信息在 这里进行初始化，
    // 你能够把客户端详情信息写死在这里或者是通过数据库来存储调取详情信息。
    // 需要配置ClientDetailsServiceConfigurer类
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        // 客户端信息存在内存中
//        // clients.withClientDetails(clientDetailsService);
//        clients.inMemory()// 使用in‐memory存储
//                .withClient("c1")// client_id  用来标识客户端
//                .secret(new BCryptPasswordEncoder().encode("secret")) // 客户端密码
//                .resourceIds("res1") // 资源列表
//                .authorizedGrantTypes("authorization_code", "password", "client_credentials", "implicit", "refresh_token")// 该client允许的授权类型 即各种授权的类型authorization_code,password,refresh_token,implicit,client_credentials
//                .scopes("all")// 允许的授权范围
//                .autoApprove(false) //加上验证回调地址
//                .redirectUris("http://www.baidu.com");
        clients.withClientDetails(clientDetailsService);
    }

    // 使用jdbc，将客户端信息存储在关系型数据表中
    @Bean
    public ClientDetailsService clientDetailsService(DataSource dataSource) {
        ClientDetailsService clientDetailsService = new JdbcClientDetailsService(dataSource);
        ((JdbcClientDetailsService) clientDetailsService).setPasswordEncoder(passwordEncoder);
        return clientDetailsService;
    }
```

##### 2. 管理令牌：返回一个AuthorizationServerTokenService

有三种token：`InMemoryTokenStore，JdbcTokenStore，JwtTokenStore`

另外定制一个TokenConfig类

```java
@Configuration
public class TokenConfig {

    //private String SIGNING_KEY = "uaa123";
    @Bean
    public TokenStore tokenStore() {
    //    return new JwtTokenStore(accessTokenConverter());
        // 不再使用内存存储令牌（普通令牌）
        return new InMemoryTokenStore();
    }

    
    // 以下是jwttokenStore的增强配置
 //   @Bean
   // public JwtAccessTokenConverter accessTokenConverter() {
     //   JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
       // converter.setSigningKey(SIGNING_KEY); //对称秘钥，资源服务器使用该秘钥来验证
        //return converter;
    //}
}

```

然后在总的授权配置类中==配置AuthorizationServerTokenService==

```java
  /*****************************************************************************************/
    /*****************************************************************************************/
    //  第二、管理令牌，即令牌管理服务
    // 令牌可以被用来 加载身份信息，里面包含了这个令牌的相关权限。
    //  用来修改令牌的格式和令牌的存储

    @Autowired 
    private TokenStore tokenStore;

    @Bean
    public AuthorizationServerTokenServices tokenService() {
        DefaultTokenServices service = new DefaultTokenServices();
        service.setClientDetailsService(clientDetailsService); //客户端信息服务
        service.setSupportRefreshToken(true);//是否产生刷新令牌


        // 令牌增强，令牌使用得tokenStore不再是remote那个了
        service.setTokenStore(tokenStore);// 令牌存储策略
     //   TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
     //   tokenEnhancerChain.setTokenEnhancers(Arrays.asList(accessTokenConverter));
     //   service.setTokenEnhancer(tokenEnhancerChain);


        service.setAccessTokenValiditySeconds(7200); // 令牌默认有效期2小时
        service.setRefreshTokenValiditySeconds(259200); // 刷新令牌默认有效期3天
        return service;
    }
```

##### 3. 令牌访问端点配置

```java
  /*****************************************************************************************/
    /*****************************************************************************************/
    // 第三 配置令牌服务和令牌endpoint的配置
    // AuthorizationServerEndpointsConfigurer用来配置令牌服务和令牌endpoint的配置
    // 用来配置令牌（token）的访问端点和令牌服务(token services)
    @Resource
    private AuthorizationCodeServices authorizationCodeServices; // 授权码模式  注入即可，不需要自己去实现
    @Resource
    private AuthenticationManager authenticationManager; // 密码模式
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                .authenticationManager(authenticationManager)// 密码模式需要
                .authorizationCodeServices(authorizationCodeServices)// 授权码模式需要
                .tokenServices(tokenService())//令牌管理服务
                .allowedTokenEndpointRequestMethods(HttpMethod.POST);//允许断点post提交
    }

//  授权码不再存储到内存
//    @Bean
//    public AuthorizationCodeServices authorizationCodeServices() {
//        //设置授权码模式的授权码如何 存取，暂时采用内存方式
//       return new InMemoryAuthorizationCodeServices();
//    }

// 这里只是对应授权码模式而已，这里将授权码存储到了数据库
    // 授权码存在数据库
    @Bean
    public AuthorizationCodeServices authorizationCodeServices(DataSource dataSource) {
        return new JdbcAuthorizationCodeServices(dataSource);//设置授权码模式的授权码如何存取
    }
```

具体见笔记

##### 4. 令牌端点的安全约束

```java
  /*****************************************************************************************/
    /*****************************************************************************************/
    // 第四、令牌访问endpoint的安全约束
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.tokenKeyAccess("permitAll()")
                .checkTokenAccess("permitAll()")
                .allowFormAuthenticationForClients();  // 允许表单认证
    }
```

##### 5. web安全配置（即spring security的配置）

用户信息   安全拦截机制等

即security中的配置

##### 6. 客户端的四种模式

授权码模式

简化模式

密码模式

客户端模式

#### 4.4 资源服务

<img src="Spring-Security%E5%BE%AE%E6%9C%8D%E5%8A%A1%E8%A7%A3%E5%86%B3%E6%96%B9%E6%A1%88.assets/image-20210707102136905.png" alt="image-20210707102136905" style="zoom:80%;" />

#### 4.5 jwt令牌

怎么使用见笔记

==资源服务中要配置该token服务==

## 强烈要求看Spring Security实现分布式系统授权的方案

见笔记的的相应部分

