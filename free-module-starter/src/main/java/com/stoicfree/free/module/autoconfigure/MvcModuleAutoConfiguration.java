package com.stoicfree.free.module.autoconfigure;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.servlet.HandlerInterceptor;

import com.stoicfree.free.mvc.module.config.MvcProperties;
import com.stoicfree.free.mvc.module.filter.CrossDomainFilter;
import com.stoicfree.free.mvc.module.filter.RequestWrapperFilter;
import com.stoicfree.free.mvc.module.interceptor.InterceptorWebMvcConfigure;
import com.stoicfree.free.mvc.module.interceptor.TimeCostInterceptor;

/**
 * @author zengzhifei
 * @date 2023/2/3 17:14
 */
@Configuration
@ConditionalOnWebApplication
@ConditionalOnExpression("${free.mvc.enable:false}")
@EnableConfigurationProperties({MvcProperties.class})
public class MvcModuleAutoConfiguration {
    private final List<HandlerInterceptor> interceptors = new ArrayList<>();
    @Autowired
    private MvcProperties mvcProperties;

    @Bean
    public FilterRegistrationBean<RequestWrapperFilter> requestWrapperFilterBean() {
        FilterRegistrationBean<RequestWrapperFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new RequestWrapperFilter());
        bean.addUrlPatterns("/*");
        bean.setOrder(0);
        return bean;
    }

    @Bean
    public FilterRegistrationBean<CrossDomainFilter> crossDomainFilterBean() {
        FilterRegistrationBean<CrossDomainFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new CrossDomainFilter(mvcProperties.getCrossDomain()));
        bean.addUrlPatterns("/*");
        bean.setOrder(1);
        return bean;
    }

    @Bean
    public TimeCostInterceptor timeCostInterceptor() {
        TimeCostInterceptor interceptor = new TimeCostInterceptor();
        interceptors.add(interceptor);
        return interceptor;
    }

    @Bean
    @DependsOn({"timeCostInterceptor"})
    @ConditionalOnMissingBean
    public InterceptorWebMvcConfigure interceptorWebMvcConfigure() {
        return new InterceptorWebMvcConfigure(interceptors);
    }
}