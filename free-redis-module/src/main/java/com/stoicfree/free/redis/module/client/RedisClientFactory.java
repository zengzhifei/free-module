package com.stoicfree.free.redis.module.client;

import java.lang.reflect.Proxy;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.stoicfree.free.redis.module.config.RedisProperties;

import cn.hutool.core.lang.Assert;
import redis.clients.jedis.JedisPool;

/**
 * @author zengzhifei
 * @date 2023/2/14 16:09
 */
public class RedisClientFactory implements InitializingBean, FactoryBean<RedisClient> {
    private RedisClient redisClient;
    private JedisPool jedisPool;
    @Autowired
    private RedisProperties redisProperties;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(jedisPool, "jedisPool not be null");
    }

    @Override
    public RedisClient getObject() throws Exception {
        if (redisClient != null) {
            return redisClient;
        }
        synchronized(this) {
            if (redisClient == null) {
                RedisInvocationHandler invocationHandler = new RedisInvocationHandler();
                invocationHandler.setJedisPool(jedisPool);
                invocationHandler.setRedisProperties(redisProperties);
                redisClient = (RedisClient) Proxy.newProxyInstance(getClass().getClassLoader(),
                        new Class[] {RedisClient.class}, invocationHandler);
            }
        }

        return redisClient;
    }

    @Override
    public Class<?> getObjectType() {
        return RedisClient.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }
}
