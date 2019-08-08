package com.zhao.upms.web.security;

import com.zhao.dao.mapper.SysPermissionMapper;
import com.zhao.dao.mapper.SysRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;

import java.util.Arrays;


/**
 * SpringSecurity的配置
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled=true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

//    @Autowired
//    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;
//    @Autowired
//    private RestfulAccessDeniedHandler restfulAccessDeniedHandler;
//    @Autowired
//    private CustomAccessDecisionManager customAccessDecisionManager;
    @Autowired
    private SysPermissionMapper sysPermissionMapper;
    @Autowired
    private SysRoleMapper sysRoleMapper;
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf()
                .disable()
                .sessionManagement()// 基于token，所以不需要session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//                .and()
//                .authorizeRequests()
//                .antMatchers("/user/loginPwd", "/admin/register")// 对登录注册要允许匿名访问
//                .permitAll()
//                .antMatchers(HttpMethod.OPTIONS)//跨域请求会先进行一次options请求
//                .permitAll()
//                .antMatchers("/oauth/**").permitAll()
////                .antMatchers("/**")//测试时全部运行访问
////                .permitAll()
//                .anyRequest()// 除上面外的所有请求全部需要鉴权认证
//                .authenticated();
        // 禁用缓存
        httpSecurity.headers().cacheControl();

//        httpSecurity.addFilterAfter(customFilterSecurityInterceptor(), ExceptionTranslationFilter.class);

        //添加自定义未授权和未登录结果返回
//        httpSecurity.exceptionHandling()
//                .accessDeniedHandler(restfulAccessDeniedHandler)
//                .authenticationEntryPoint(restAuthenticationEntryPoint);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(HttpMethod.GET, // 允许对于网站静态资源的无授权访问
                "/",
                "/*.html",
                "/favicon.ico",
                "/**/*.html",
                "/**/*.css",
                "/**/*.js",
                "/swagger-resources/**",
                "/v2/api-docs/**");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }

//    @Bean
//    public CustomFilterSecurityIntercepor customFilterSecurityInterceptor() {
//        CustomFilterSecurityIntercepor fsi = new CustomFilterSecurityIntercepor();
//        fsi.setAccessDecisionManager(customAccessDecisionManager);
//        fsi.setSecurityMetadataSource(securityMetadataSource());
//        return fsi;
//    }

    @Bean
    public FilterInvocationSecurityMetadataSource securityMetadataSource() {
        return new CustomSecurityMetadataSource(sysPermissionMapper, sysRoleMapper);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManager() {
        AuthenticationManager authenticationManager = new ProviderManager(Arrays.asList(authenticationProvider()));
        return authenticationManager;
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

}
