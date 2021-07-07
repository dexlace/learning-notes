package com.security.distributed.gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;

/**
 * @Author: xiaogongbing
 * @Description:
 * @Date: 2021/4/20
 */
@Configuration
public class ResourceServerConfig {
    public static final String RESOURCE_ID = "res1";

    /*** 统一认证服务(UAA) 资源拦截 */

    @Configuration
    @EnableResourceServer
    public class UAAServerConfig extends ResourceServerConfigurerAdapter {
        @Autowired
        private TokenStore tokenStore;
        @Override
        public void configure(ResourceServerSecurityConfigurer resources){
            resources.tokenStore(tokenStore).resourceId(RESOURCE_ID)
                    .stateless(true);
        }

        /**
         * 指定了若请求匹配/uaa/**网关不进行拦截。
         * @param http
         * @throws Exception
         */
        @Override
        public void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests()
                    .antMatchers("/uaa/**").permitAll();
        }
    }


    /**
     * 订单服务拦截
     *指定了若请求匹配/order/**，也就是访问统一用户服务，接入客户端需要有scope中包含 read，并且authorities(权限)中需要包含ROLE_USER。
     */
    @Configuration
    @EnableResourceServer
    public class OrderServerConfig extends ResourceServerConfigurerAdapter {
        @Autowired
        private TokenStore tokenStore;
        @Override
        public void configure(ResourceServerSecurityConfigurer resources) {
            resources.tokenStore(tokenStore).resourceId(RESOURCE_ID)
                    .stateless(true);
        }

        /**
         * 指定了若请求匹配/order/**，也就是访问统一用户服务，接入客户端需要有scope中包含 read，并且authorities(权限)中需要包含ROLE_USER。
         * @param http
         * @throws Exception
         */
        @Override
        public void configure(HttpSecurity http) throws Exception {
            http .authorizeRequests()
                    .antMatchers("/order/**").access("#oauth2.hasScope('ROLE_ADMIN')");
        }
    }



}
