package com.stoicfree.free.module.core.mvc.config;

import java.time.Duration;

import lombok.Data;
import lombok.ToString;

/**
 * @author zengzhifei
 * @date 2023/2/11 14:46
 */
@Data
@ToString(callSuper = true)
public class PassportProperties extends BaseInterceptorProperties {
    private boolean enable = false;
    private String tokenKey;
    private Duration expires;
}
