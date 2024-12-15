package cc.flyfree.free.module.core.mvc.filter;

import java.io.IOException;

import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.resource.ResourceUrlEncodingFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import cc.flyfree.free.module.core.mvc.wrapper.BizHttpServletRequestWrapper;
import cn.hutool.http.ContentType;
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
public class RequestWrapperFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String contentType = request.getContentType();

        // 处理multipart/form-data
        if (contentType != null && contentType.contains(ContentType.MULTIPART.getValue())) {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            request = new StandardServletMultipartResolver().resolveMultipart(httpServletRequest);
        }

        // 可重复写response
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(httpServletResponse);

        try {
            // 处理 application/x-www-form-urlencoded
            if (contentType != null && contentType.contains(ContentType.FORM_URLENCODED.getValue())) {
                ResourceUrlEncodingFilter resourceUrlEncodingFilter = new ResourceUrlEncodingFilter();
                resourceUrlEncodingFilter.doFilter(request, responseWrapper, chain);
                return;
            }

            // 处理其他类型
            if (request instanceof HttpServletRequest httpServletRequest) {
                BizHttpServletRequestWrapper requestWrapper = new BizHttpServletRequestWrapper(httpServletRequest);
                chain.doFilter(requestWrapper, responseWrapper);
            } else {
                chain.doFilter(request, responseWrapper);
            }
        } finally {
            responseWrapper.copyBodyToResponse();
        }
    }

    @Override
    public void destroy() {
    }
}
