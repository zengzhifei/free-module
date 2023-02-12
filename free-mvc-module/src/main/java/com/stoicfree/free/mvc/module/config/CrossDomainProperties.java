package com.stoicfree.free.mvc.module.config;

import lombok.Data;

/**
 * @author zengzhifei
 * @date 2023/2/11 14:47
 */
@Data
public class CrossDomainProperties {
    private String host = "*";
    private String headers;
}
