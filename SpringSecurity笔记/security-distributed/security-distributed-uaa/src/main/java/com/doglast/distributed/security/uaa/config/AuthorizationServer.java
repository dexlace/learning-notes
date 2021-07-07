package com.doglast.distributed.security.uaa.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.InMemoryAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Arrays;

/**
 * @Author: xiaogongbing
 * @Description:
 * @Date: 2021/4/18
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServer extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private TokenStore tokenStore;
    @Autowired
    private ClientDetailsService clientDetailsService;

    @Resource
    private AuthorizationCodeServices authorizationCodeServices;
    @Resource
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtAccessTokenConverter accessTokenConverter;

    @Autowired
    private PasswordEncoder passwordEncoder;



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
//                .withClient("c1")// client_id
//                .secret(new BCryptPasswordEncoder().encode("secret"))
//                .resourceIds("res1") // 资源列表
//                .authorizedGrantTypes("authorization_code", "password", "client_credentials", "implicit", "refresh_token")// 该client允许的授权类型 authorization_code,password,refresh_token,implicit,client_credentials
//                .scopes("all")// 允许的授权范围
//                .autoApprove(false) //加上验证回调地址
//                .redirectUris("http://www.baidu.com");
        clients.withClientDetails(clientDetailsService);
    }

    @Bean
    public ClientDetailsService clientDetailsService(DataSource dataSource) {
        ClientDetailsService clientDetailsService = new JdbcClientDetailsService(dataSource);
        ((JdbcClientDetailsService) clientDetailsService).setPasswordEncoder(passwordEncoder);
        return clientDetailsService;
    }
    /*****************************************************************************************/
    /*****************************************************************************************/
    //  第二、管理令牌，即令牌管理服务
    // 令牌可以被用来 加载身份信息，里面包含了这个令牌的相关权限。
    //  用来修改令牌的格式和令牌的存储
    @Bean
    public AuthorizationServerTokenServices tokenService() {
        DefaultTokenServices service = new DefaultTokenServices();
        service.setClientDetailsService(clientDetailsService); //客户端信息服务
        service.setSupportRefreshToken(true);//是否产生刷新令牌


        // 令牌增强，令牌使用得tokenStore不再是remote那个了
        service.setTokenStore(tokenStore);// 令牌存储策略
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(accessTokenConverter));
        service.setTokenEnhancer(tokenEnhancerChain);


        service.setAccessTokenValiditySeconds(7200); // 令牌默认有效期2小时
        service.setRefreshTokenValiditySeconds(259200); // 刷新令牌默认有效期3天
        return service;
    }
    /*****************************************************************************************/
    /*****************************************************************************************/
    // 第三 配置令牌服务和令牌endpoint的配置
    // AuthorizationServerEndpointsConfigurer用来配置令牌服务和令牌endpoint的配置
    // 用来配置令牌（token）的访问端点和令牌服务(token services)
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
    /*****************************************************************************************/
    /*****************************************************************************************/
    // 第四、令牌访问endpoint的安全约束
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.tokenKeyAccess("permitAll()")
                .checkTokenAccess("permitAll()")
                .allowFormAuthenticationForClients();  // 允许表单认证
    }

}
