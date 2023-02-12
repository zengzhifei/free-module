package com.stoicfree.free.mvc.module.config;

import lombok.Data;

/**
 * @author zengzhifei
 * @date 2023/2/11 14:46
 */
@Data
public class TimeCostProperties {
    private int order = 1;
    private String addPaths;
    private String excludePaths;
}
