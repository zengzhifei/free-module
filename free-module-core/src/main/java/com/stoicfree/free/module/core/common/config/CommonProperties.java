package com.stoicfree.free.module.core.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import lombok.Data;

/**
 * @author zengzhifei
 * @date 2023/2/15 19:03
 */
@Data
@ConfigurationProperties(prefix = "free.common")
public class CommonProperties {
    /**
     * 日志等级自定义，class:level
     */
    private String loggingLevels;

    /**
     * 邮箱配置
     */
    @NestedConfigurationProperty
    private MailProperties mail;

    /**
     * 重试任务配置
     */
    @NestedConfigurationProperty
    private RetryProperties retry = new RetryProperties();
}
