package com.opensource.api.order.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by amg871 on 5/24/17.
 */
@Component
public class SimpleLoggingFilter implements Filter {
    private final Logger logger = LoggerFactory.getLogger(SimpleLoggingFilter .class);

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;
        String url = request.getServletPath();
        chain.doFilter(req, res);

//        response.getHeaderNames().forEach(str -> System.out.println(str));
        logger.info(url + " " + request.getMethod() + " " + response.getStatus());
    }

    public void init(FilterConfig filterConfig) {}

    public void destroy() {}

}
