package com.stoicfree.free.module.core.common.config;

import lombok.Data;

/**
 * @author zengzhifei
 * @date 2023/2/20 12:47
 */
@Data
public class RetryProperties {
    private String crontab = "* * * * *";
}