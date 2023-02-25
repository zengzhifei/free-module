package com.stoicfree.free.module.core.redis.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collections;

import com.stoicfree.free.module.core.redis.config.RedisProperties;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;

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
        String methodName = method.getName();
        try (Jedis jedis = jedisPool.getResource()) {
            Object ret;
            if ("lock".equals(methodName)) {
                ret = jedis.set((String) args[0], (String) args[1], SetParams.setParams().nx().px((long) args[2]));
            } else if ("unlock".equals(methodName)) {
                String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) "
                        + "else return 0 end";
                ret = jedis.eval(luaScript, Collections.singletonList((String) args[0]),
                        Collections.singletonList((String) args[1]));
            } else {
                ret = method.invoke(jedis, args);
            }
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
