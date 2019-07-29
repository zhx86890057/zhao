package com.zhao.upms.web.config;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 解决ajax跨域
 */
@Component
public class CorsFilter implements Filter {

    public void doFilter(ServletRequest req, ServletResponse resp,
                         FilterChain chain) throws IOException, ServletException {
		//设置跨域请求
		HttpServletResponse response = (HttpServletResponse) resp;
		// 这里最好不要写通配符，如果允许多个域请求数据的话，可以直接用逗号隔开："http://www.baidu.com,http://google.com"
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Methods", "GET, HEAD, POST, PUT, DELETE, TRACE, OPTIONS, PATCH");
		response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type,Token,Accept, Connection, User-Agent, Cookie");
		response.setHeader("Access-Control-Max-Age", "3600");

		chain.doFilter(req, response);
    		  
    }

    public void init(FilterConfig filterConfig) {}
    public void destroy() {}
}