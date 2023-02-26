package com.stoicfree.free.module.core.rpc.config;

import lombok.Data;

/**
 * @author zengzhifei
 * @date 2023/2/26 10:42
 */
@Data
public class ConsumerProperties {
    /**
     * 产品线
     */
    private String productId;
    /**
     * 连接超时
     */
    private int connectionTimeout = 10000;
    /**
     * 读取超时
     */
    private int readTimeout = 10000;
}
