package com.stoicfree.free.module.core.redis.config;

import java.time.Duration;

import org.springframework.boot.context.properties.NestedConfigurationProperty;

import lombok.Data;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author zengzhifei
 * @date 2023/2/14 17:00
 */
@Data
public class RedisClientConfig {
    private int database = 0;
    private String host = "localhost";
    private String password;
    private int port = 6379;
    private boolean ssl;
    private Duration timeout;
    @NestedConfigurationProperty
    private JedisPoolConfig jedisPool;
}
