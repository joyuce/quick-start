package com.j.openproject.filter;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Joyuce
 * @Type TestFilter
 * @Desc
 * @date 2019年11月22日
 * @Version V1.0
 */
@Slf4j
@Order(1)
@Component
@WebFilter(urlPatterns = "/*", filterName = "testFilter", asyncSupported = true)
public class TestFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        //log.info("sasasa");
        chain.doFilter(request, response);
    }

}
