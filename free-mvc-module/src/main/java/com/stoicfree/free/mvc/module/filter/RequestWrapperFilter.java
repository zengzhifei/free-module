package com.stoicfree.free.mvc.module.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.stoicfree.free.mvc.module.wrapper.BizHttpServletRequestWrapper;

/**
 * @author zengzhifei
 * @date 2019/9/7 17:31
 */
public class RequestWrapperFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        chain.doFilter(new BizHttpServletRequestWrapper((HttpServletRequest) request), response);
    }

    @Override
    public void destroy() {
    }
}
