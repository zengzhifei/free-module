package com.stoicfree.free.common.module.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * @author zengzhifei
 * @date 2023/2/15 19:03
 */
@Data
@ConfigurationProperties(prefix = "free.common")
public class CommonProperties {
    private String loggingLevels;
}
