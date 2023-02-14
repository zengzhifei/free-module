package com.stoicfree.free.redis.module.config;

import com.stoicfree.free.redis.module.client.RedisClientFactory;

import redis.clients.jedis.JedisPool;

/**
 * @author zengzhifei
 * @date 2023/2/14 17:01
 */
public abstract class AbstractRedisConfig {
    public JedisPool buildJedisPool(RedisClientConfig config) {
        return new JedisPool(config.getJedisPool(), config.getHost(), config.getPort(),
                config.getTimeout() != null ? (int) config.getTimeout().toMillis() : 0,
                config.getPassword(), config.getDatabase(), config.isSsl());
    }

    public RedisClientFactory buildRedisClientFactory(JedisPool jedisPool) {
        RedisClientFactory redisClientFactory = new RedisClientFactory();
        redisClientFactory.setJedisPool(jedisPool);
        return redisClientFactory;
    }
}
