package com.stoicfree.free.module.core.mvc.config;

import lombok.Data;

/**
 * @author zengzhifei
 * @date 2023/2/11 14:46
 */
@Data
public class BaseInterceptorProperties {
    private int order = 1;
    private String addPaths;
    private String excludePaths;
}
