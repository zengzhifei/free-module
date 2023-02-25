package com.stoicfree.free.module.autoconfigure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.stoicfree.free.module.core.elastic.config.EsProperties;

/**
 * @author zengzhifei
 * @date 2023/2/3 17:14
 */
@Configuration
@ConditionalOnExpression("${free.es.enable:false}")
@EnableConfigurationProperties({EsProperties.class})
public class EsModuleAutoConfiguration {
    @Autowired
    private EsProperties esProperties;
}
