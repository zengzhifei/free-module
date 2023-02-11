package com.stoicfree.free.mvc.module.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * @author zengzhifei
 * @date 2023/2/11 14:46
 */
@Data
@ConfigurationProperties(prefix = "free.mvc.time-cost")
public class TimeCostProperties {
    private boolean enable = true;
    private int order = 1;
    private String addPaths;
    private String excludePaths;
}
