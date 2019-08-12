package com.zhao.upms.web.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

/**
 * Created by ben on 2018/5/24.
 * @EnableResourceServer注解实际上相当于加上OAuth2AuthenticationProcessingFilter过滤器
 */
@Configuration
@EnableResourceServer
@EnableOAuth2Client
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Autowired
    RestfulAccessDeniedHandler restfulAccessDeniedHandler;

    @Autowired
    RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    @Autowired
    private CustomAccessDecisionManager customAccessDecisionManager;
    @Autowired
    private FilterInvocationSecurityMetadataSource securityMetadataSource;

    /**
     * 与资源安全配置相关
     *
     * @param resources
     */
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.accessDeniedHandler(restfulAccessDeniedHandler);
        resources.resourceId("auth-server");
        resources.authenticationEntryPoint(restAuthenticationEntryPoint);
    }

    public void configure(HttpSecurity http) throws Exception {
        http.requestMatchers().antMatchers("/user/loginPwd")
                .and().authorizeRequests().antMatchers("/user/loginPwd").permitAll();
//        http.authorizeRequests().antMatchers("/user/loginPwd", "/user/logout").permitAll()// 对登录注册要允许匿名访问
//                .antMatchers(HttpMethod.OPTIONS).permitAll()//跨域请求会先进行一次options请求
//                .antMatchers("/oauth/**").permitAll()
//                .antMatchers("/druid/**").permitAll()
//                .anyRequest().authenticated();//其他需要授权用户
//        http.authorizeRequests().anyRequest().permitAll();  //其他的不需要登陆

        CustomFilterSecurityIntercepor fsi = new CustomFilterSecurityIntercepor();
        fsi.setAccessDecisionManager(customAccessDecisionManager);
        fsi.setSecurityMetadataSource(securityMetadataSource);
        http.addFilterAfter(fsi, FilterSecurityInterceptor.class);
    }
}
