package com.stoicfree.free.module.core.rpc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import lombok.Data;

/**
 * @author zengzhifei
 * @date 2023/2/11 14:47
 */
@Data
@ConfigurationProperties(prefix = "free.rpc")
public class RpcProperties {
    @NestedConfigurationProperty
    private ProviderProperties provider;

    @NestedConfigurationProperty
    private ConsumerProperties consumer;
}
