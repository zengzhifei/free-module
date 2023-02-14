package com.stoicfree.free.redis.module.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.stoicfree.free.redis.module.config.RedisProperties;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @author zengzhifei
 * @date 2023/2/14 16:21
 */
@Slf4j
public class RedisInvocationHandler implements InvocationHandler {
    private JedisPool jedisPool;
    private RedisProperties redisProperties;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try (Jedis jedis = jedisPool.getResource()) {
            Object ret = method.invoke(jedis, args);
            if (redisProperties.isLog()) {
                log.info("redis: {}, args: {}, ret: {}", method.getName(), args, ret);
            }
            return ret;
        }
    }

    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public void setRedisProperties(RedisProperties redisProperties) {
        this.redisProperties = redisProperties;
    }
}
