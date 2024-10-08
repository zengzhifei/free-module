/*
 * @author: yuzhijia01
 * @description: 描述信息
 * @Date: 2021-04-21 16:19:08
 * @LastEditTime: 2021-04-21 16:19:10
 */
/*
 * Copyright (C) 2018 Baidu, Inc. All Rights Reserved.
 */
package cc.flyfree.free.module.core.mvc.filter;

import java.io.IOException;
import java.net.URL;

import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import cc.flyfree.free.module.core.mvc.config.CrossDomainProperties;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author zengzhifei
 * @date 2019/9/7 17:31
 */
public class CrossDomainFilter implements Filter {
    private final CrossDomainProperties crossDomainProperties;

    public CrossDomainFilter(CrossDomainProperties crossDomainProperties) {
        this.crossDomainProperties = crossDomainProperties;
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String requestOrigin = request.getHeader("Origin");
        if (requestOrigin != null) {
            URL url = new URL(requestOrigin);
            String host = crossDomainProperties.getHost();
            if (url.getProtocol() != null && url.getHost() != null && url.getHost().endsWith(host)) {
                response.addHeader("Access-Control-Allow-Origin", requestOrigin);
            }
        }
        String headers = "";
        if (StringUtils.isNotBlank(crossDomainProperties.getHeaders())) {
            headers = ", " + crossDomainProperties.getHeaders();
        }
        response.addHeader("Access-Control-Allow-Credentials", "true");
        response.addHeader("Access-Control-Allow-Methods", "PUT,POST,GET,DELETE,OPTIONS");
        response.addHeader("Access-Control-Allow-Headers",
                "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With" + headers);
        if (request.getMethod().equals(RequestMethod.OPTIONS.name())) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
    }
}