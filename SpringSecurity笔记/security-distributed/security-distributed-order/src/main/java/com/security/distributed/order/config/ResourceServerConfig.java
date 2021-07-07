package com.security.distributed.order.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;

/**
 * @Author: xiaogongbing
 * @Description:
 * @Date: 2021/4/19
 */
@Configuration
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    public static final String RESOURCE_ID = "res1";

    @Autowired
    TokenStore tokenStore;
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.resourceId(RESOURCE_ID)
                // 不再使用tokenServices
//                .tokenServices(tokenService())
                // 直接使用tokenStore
                .tokenStore(tokenStore)
                .stateless(true);

    }

    // 不再使用remoteTokenService
//    //资源服务令牌解析服务
//    // 使用 RemoteTokenServices 资源服务器通过 HTTP 请求来解码令牌，
//    // 每次都请求授权服务器端点 /oauth/check_token
//    @Bean
//    public ResourceServerTokenServices tokenService() {
//        //使用远程服务请求授权服务器校验token,必须指定校验token 的url、client_id，client_secret
//        RemoteTokenServices service = new RemoteTokenServices();
//        service.setCheckTokenEndpointUrl("http://localhost:8088/uaa/oauth/check_token");
//        service.setClientId("c1");
//        service.setClientSecret("secret");
//        return service;
//    }


    @Override
    public void configure(HttpSecurity http) throws Exception {

        http
                .authorizeRequests()
                .antMatchers("/**").access("#oauth2.hasScope('ROLE_ADMIN')")
                .and().csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }




}
