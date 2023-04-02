package com.stoicfree.free.module.core.stream.handler;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Map;

import com.stoicfree.free.module.core.redis.client.RedisClient;
import com.stoicfree.free.module.core.stream.exception.StreamException;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.IoUtil;
import redis.clients.jedis.StreamEntryID;

/**
 * @author zengzhifei
 * @date 2023/3/31 23:51
 */
public abstract class BaseHandler implements CommandHandler {
    @Override
    public CommandHandler validate(SelectionKey selectionKey, SocketChannel channel) {
        boolean isAuth = Convert.toBool(selectionKey.attachment(), false);
        if (!isAuth) {
            IoUtil.close(channel);
            throw new StreamException("stream auth fail");
        }
        return this;
    }

    protected StreamEntryID publish(RedisClient client, String pipe, Map<String, String> hash) {
        return client.xadd(pipe, StreamEntryID.NEW_ENTRY, hash, Long.MAX_VALUE, true);
    }
}
