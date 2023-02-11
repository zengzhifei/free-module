package com.stoicfree.free.mvc.module.interceptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.stoicfree.free.mvc.module.config.TimeCostProperties;

/**
 * @author zengzhifei
 * @date 2023/2/11 14:46
 */
public class InterceptorWebMvcConfigure implements WebMvcConfigurer {
    private final List<HandlerInterceptor> interceptors;
    @Autowired
    private TimeCostProperties timeCostProperties;

    public InterceptorWebMvcConfigure(List<HandlerInterceptor> interceptors) {
        this.interceptors = interceptors == null ? new ArrayList<>(0) : interceptors;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        for (HandlerInterceptor interceptor : interceptors) {
            // 耗时拦截器
            if (interceptor instanceof TimeCostInterceptor) {
                InterceptorRegistration registration =
                        registry.addInterceptor(interceptor).order(timeCostProperties.getOrder());
                if (StringUtils.isNotBlank(timeCostProperties.getAddPaths())) {
                    registration.addPathPatterns(Arrays.asList(timeCostProperties.getAddPaths().split(",")));
                }
                if (StringUtils.isNotBlank(timeCostProperties.getExcludePaths())) {
                    registration.excludePathPatterns(Arrays.asList(timeCostProperties.getExcludePaths().split(",")));
                }
            }
        }
    }
}
