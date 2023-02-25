package com.stoicfree.free.module.core.mvc.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.stoicfree.free.module.core.mvc.interceptor.SecurityInterceptor;
import com.stoicfree.free.module.core.mvc.interceptor.TimeCostInterceptor;

/**
 * @author zengzhifei
 * @date 2023/2/11 14:46
 */
public class InterceptorWebMvcConfigure implements WebMvcConfigurer {
    @Autowired
    private MvcProperties mvcProperties;

    private final List<HandlerInterceptor> interceptors;

    public InterceptorWebMvcConfigure(List<HandlerInterceptor> interceptors) {
        this.interceptors = interceptors == null ? new ArrayList<>(0) : interceptors;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        for (HandlerInterceptor interceptor : interceptors) {
            // 耗时拦截器
            if (interceptor instanceof TimeCostInterceptor) {
                addInterceptorAndSetProperties(registry, interceptor, mvcProperties.getTimeCost());
            }
            // 登录拦截器
            if (interceptor instanceof SecurityInterceptor) {
                addInterceptorAndSetProperties(registry, interceptor, mvcProperties.getSecurity());
            }
        }
    }

    private void addInterceptorAndSetProperties(InterceptorRegistry registry, HandlerInterceptor interceptor,
                                                InterceptorProperties properties) {
        InterceptorRegistration registration = registry.addInterceptor(interceptor).order(properties.getOrder());
        if (StringUtils.isNotBlank(properties.getAddPaths())) {
            registration.addPathPatterns(Arrays.asList(properties.getAddPaths().split(",")));
        }
        if (StringUtils.isNotBlank(properties.getExcludePaths())) {
            registration.excludePathPatterns(Arrays.asList(properties.getExcludePaths().split(",")));
        }
    }
}
