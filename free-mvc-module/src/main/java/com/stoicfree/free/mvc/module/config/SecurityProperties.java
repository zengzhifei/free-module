package com.stoicfree.free.mvc.module.config;

import java.time.Duration;

import lombok.Data;
import lombok.ToString;

/**
 * @author zengzhifei
 * @date 2023/2/11 14:46
 */
@Data
@ToString(callSuper = true)
public class SecurityProperties extends InterceptorProperties {
    private boolean enable = false;
    private String tokenKey;
    private Duration expires;
}
