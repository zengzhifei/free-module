package com.stoicfree.free.module.core.stream.handler;

import java.util.Map;

import com.stoicfree.free.module.core.redis.client.RedisClient;

import redis.clients.jedis.StreamEntryID;

/**
 * @author zengzhifei
 * @date 2023/3/31 23:51
 */
public abstract class BaseHandler {
    protected StreamEntryID publish(RedisClient client, String pipe, Map<String, String> hash) {
        return client.xadd(pipe, StreamEntryID.NEW_ENTRY, hash, Integer.MAX_VALUE, true);
    }
}
