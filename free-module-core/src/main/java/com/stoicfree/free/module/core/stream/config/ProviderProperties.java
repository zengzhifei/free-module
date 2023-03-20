package com.stoicfree.free.module.core.stream.config;

import lombok.Data;

/**
 * @author zengzhifei
 * @date 2023/2/26 10:42
 */
@Data
public class ProviderProperties {
    /**
     * pipe名称
     */
    private String pipe;
    /**
     * pipe密码
     */
    private String password;
    /**
     * 最大长度
     */
    private long maxLen = Long.MAX_VALUE;
}
