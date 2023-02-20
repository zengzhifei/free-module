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
    /**
     * 是否开启es
     */
    private boolean enable = false;
}
