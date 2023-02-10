package com.stoicfree.free.common.module.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.stoicfree.free.mvc.module.filter.BaseFilter;

/**
 * @author zengzhifei
 * @date 2023/2/3 17:14
 */
@Configuration
public class FreeMvcModuleAutoConfiguration {
    @Bean
    @ConditionalOnProperty(value = {"free.module.mvc.baseFilter.enable"}, havingValue = "true")
    public BaseFilter baseFilter() {
        return new BaseFilter();
    }
}
