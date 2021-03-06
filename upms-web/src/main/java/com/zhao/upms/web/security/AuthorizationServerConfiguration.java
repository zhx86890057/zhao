package com.zhao.upms.web.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.TokenStoreUserApprovalHandler;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

/*
 * 在当前应用程序上下文中启用授权服务器(即AuthorizationEndpoint和TokenEndpoint)的便利注释，
 * 它必须是一个DispatcherServlet上下文。服务器的许多特性可以通过使用AuthorizationServerConfigurer类型的@
 * bean来定制(例如，通过扩展AuthorizationServerConfigurerAdapter)。用户负责使用正常的Spring安全特性(
 * @EnableWebSecurity等)来保护授权端点(/oauth/授权)，但是令牌端点(/oauth/
 * Token)将通过客户端凭证上的HTTP基本身份验证自动获得。
 * 客户端必须通过一个或多个AuthorizationServerConfigurers提供一个ClientDetailsService来注册。
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    RedisConnectionFactory redisConnectionFactory;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private ClientDetailsService clientDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        //配置客户端认证
        clients.inMemory().withClient("auth-server")
                .authorizedGrantTypes("refresh_token","authorization_code","password","client_credentials")     // 该client允许的授权类型
                .accessTokenValiditySeconds(7200)               // Token 的有效期
                .scopes("read")                    // 允许的授权范围
                .autoApprove(true)                  //登录后绕过批准询问(/oauth/confirm_access)
                .secret(passwordEncoder.encode("123456") )
                .redirectUris("http://127.0.0.1").resourceIds("auth");
    }
    /**
     * 配置AuthorizationServerEndpointsConfigurer众多相关类，
     * 包括配置身份认证器，配置认证方式，TokenStore，TokenGranter，OAuth2RequestFactory
     *
     * @param endpoints
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                .authenticationManager(authenticationManager)
                .userDetailsService(customUserDetailsService)
                .reuseRefreshTokens(false)
                .tokenStore(tokenStore())
                .userApprovalHandler(userApprovalHandler())
                .allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST);
    }


    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        oauthServer.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");//isAuthenticated():排除anonymous isFullyAuthenticated():排除anonymous以及remember-me
        //允许表单认证
        oauthServer.allowFormAuthenticationForClients();
    }

    @Bean
    public TokenStore tokenStore() {
        return new RedisTokenStore(redisConnectionFactory);
    }

    @Bean
    public TokenStoreUserApprovalHandler userApprovalHandler(){
        TokenStoreUserApprovalHandler handler = new TokenStoreUserApprovalHandler();
        handler.setTokenStore(tokenStore());
        handler.setRequestFactory(new DefaultOAuth2RequestFactory(clientDetailsService));
        handler.setClientDetailsService(clientDetailsService);
        return handler;
    }
}
