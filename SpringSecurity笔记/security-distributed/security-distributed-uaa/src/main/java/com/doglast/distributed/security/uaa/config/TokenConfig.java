package com.doglast.distributed.security.uaa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

/**
 * @Author: xiaogongbing
 * @Description:
 * @Date: 2021/4/18
 */
@Configuration
public class TokenConfig {

    private String SIGNING_KEY = "uaa123";
    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
        // 不再使用内存存储令牌（普通令牌）
//        return new InMemoryTokenStore();
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey(SIGNING_KEY); //对称秘钥，资源服务器使用该秘钥来验证
        return converter;
    }
}
