package com.stoicfree.free.module.core.rpc.config;

import lombok.Data;

/**
 * @author zengzhifei
 * @date 2023/2/26 10:42
 */
@Data
public class ProviderProperties {
    /**
     * 允许的产品线，多个英文逗号隔空
     */
    private String allowProductIds;
}
