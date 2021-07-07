# Spring-Security微服务解决方案

## 1.关于注册中心的服务保护

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

## 2. 关于认证服务中的Web安全配置类和资源服务配置类

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

