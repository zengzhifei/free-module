/*
 * Copyright (C) 2020 Baidu, Inc. All Rights Reserved.
 */
package cc.flyfree.free.module.core.elastic.config;

import lombok.Data;

/**
 * @author zengzhifei
 * @date 2022/8/12 15:04
 */
@Data
public class EsClientConfig {
    /**
     * ES Host
     */
    protected String host;
    /**
     * ES Port
     */
    protected Integer port;
    /**
     * ES账号
     */
    protected String user;
    /**
     * ES密码
     */
    protected String password;
    /**
     * ES 索引
     */
    protected String index;
    /**
     * 重试次数
     */
    private int retries = 3;
}
