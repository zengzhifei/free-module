package com.stoicfree.free.module.core.stream.config;

import lombok.Data;

/**
 * @author zengzhifei
 * @date 2023/2/26 10:42
 */
@Data
public class ConsumerProperties {
    /**
     * server地址
     */
    private String metaHost;
    /**
     * queue名称
     */
    private String queue;
    /**
     * queue token
     */
    private String token;
    /**
     * 单次消费数量
     */
    private int count = 1;
}
