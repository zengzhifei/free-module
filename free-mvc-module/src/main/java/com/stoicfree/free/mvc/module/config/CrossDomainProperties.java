package com.stoicfree.free.mvc.module.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * @author zengzhifei
 * @date 2023/2/11 14:47
 */
@Data
@ConfigurationProperties(prefix = "free.mvc.cross.domain")
public class CrossDomainProperties {
    private boolean enable = true;
    private String host = "*";
    private String headers;
}
