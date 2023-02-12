package com.stoicfree.free.db.module.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * @author zengzhifei
 * @date 2023/2/12 16:57
 */
@Data
@ConfigurationProperties(prefix = "free.db")
public class DbProperties {
    private boolean enable = false;
    private String entityPackage;
}