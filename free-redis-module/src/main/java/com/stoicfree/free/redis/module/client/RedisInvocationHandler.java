package com.stoicfree.free.redis.module.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @author zengzhifei
 * @date 2023/2/14 16:21
 */
public class RedisInvocationHandler implements InvocationHandler {
    private JedisPool jedisPool;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try (Jedis jedis = jedisPool.getResource()) {
            return method.invoke(jedis, args);
        }
    }

    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }
}
