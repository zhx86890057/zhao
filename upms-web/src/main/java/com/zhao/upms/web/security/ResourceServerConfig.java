package com.zhao.upms.web.security;

import com.zhao.dao.mapper.SysPermissionMapper;
import com.zhao.dao.mapper.SysRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
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
//    @Autowired
//    FilterSecurityInterceptor customFilterSecurityInterceptor;
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
//        http.authorizeRequests().antMatchers("/test").authenticated();//需要授权用户
//        http.authorizeRequests().anyRequest().permitAll();  //其他的不需要登陆
//        http.addFilterBefore(customFilterSecurityInterceptor, FilterSecurityInterceptor.class);
    }
}
