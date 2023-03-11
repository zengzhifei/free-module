package com.stoicfree.free.module.core.mvc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import lombok.Data;

/**
 * @author zengzhifei
 * @date 2023/2/12 16:03
 */
@Data
@ConfigurationProperties(prefix = "free.mvc")
public class MvcProperties {
    private boolean enable = false;

    @NestedConfigurationProperty
    private RequestWrapperProperties requestWrapper = new RequestWrapperProperties();

    @NestedConfigurationProperty
    private CrossDomainProperties crossDomain = new CrossDomainProperties();

    @NestedConfigurationProperty
    private TimeCostProperties timeCost = new TimeCostProperties();

    @NestedConfigurationProperty
    private SecurityProperties security = new SecurityProperties();

    @NestedConfigurationProperty
    private SwaggerProperties swagger = new SwaggerProperties();
}
