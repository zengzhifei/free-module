package com.stoicfree.free.mvc.module.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * @author zengzhifei
 * @date 2023/2/9 19:13
 */
@Data
@ConfigurationProperties(prefix = "free.mvc")
public class MvcModuleProperties {
    private RequestWrapperFilter requestWrapperFilter;

    @Data
    public static class RequestWrapperFilter {
        private boolean enable;
    }
}
