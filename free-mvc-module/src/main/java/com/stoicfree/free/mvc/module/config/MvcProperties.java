package com.stoicfree.free.mvc.module.config;

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
    private boolean enable = true;

    @NestedConfigurationProperty
    private RequestWrapperProperties requestWrapper = new RequestWrapperProperties();

    @NestedConfigurationProperty
    private CrossDomainProperties crossDomain = new CrossDomainProperties();

    @NestedConfigurationProperty
    private TimeCostProperties timeCost = new TimeCostProperties();
}
