package com.stoicfree.free.module.core.stream.config;

import org.springframework.boot.context.properties.NestedConfigurationProperty;

import lombok.Data;

/**
 * @author zengzhifei
 * @date 2023/3/19 22:59
 */
@Data
public class StreamProperties {
    @NestedConfigurationProperty
    private ProviderProperties provider;

    @NestedConfigurationProperty
    private ConsumerProperties consumer;
}
