package com.stoicfree.free.common.module.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.stoicfree.free.mvc.module.config.MvcModuleProperties;
import com.stoicfree.free.mvc.module.filter.RequestWrapperFilter;

/**
 * @author zengzhifei
 * @date 2023/2/3 17:14
 */
@Configuration
@ConditionalOnWebApplication
@EnableConfigurationProperties(MvcModuleProperties.class)
public class FreeMvcModuleAutoConfiguration {
    @Bean
    @ConditionalOnExpression("${free.mvc.request-wrapper-filter.enable:true}")
    public FilterRegistrationBean<RequestWrapperFilter> requestWrapperFilterBean() {
        FilterRegistrationBean<RequestWrapperFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new RequestWrapperFilter());
        bean.addUrlPatterns("/*");
        bean.setOrder(0);
        return bean;
    }
}
