package com.stoicfree.free.es.module.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * @author zengzhifei
 * @date 2023/2/12 16:57
 */
@Data
@ConfigurationProperties(prefix = "free.es")
public class EsProperties {
    private boolean enable = false;
}
