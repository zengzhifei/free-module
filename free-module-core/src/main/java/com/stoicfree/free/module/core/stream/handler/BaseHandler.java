package com.stoicfree.free.module.core.stream.handler;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Map;

import com.stoicfree.free.module.core.common.misc.socket.nio.ChannelIo;
import com.stoicfree.free.module.core.common.misc.socket.nio.protocol.Packet;
import com.stoicfree.free.module.core.common.support.Callback0;
import com.stoicfree.free.module.core.common.support.Callback2;
import com.stoicfree.free.module.core.redis.client.RedisClient;
import com.stoicfree.free.module.core.stream.exception.StreamServerException;
import com.stoicfree.free.module.core.stream.protocol.Command;

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
            throw new StreamServerException("stream auth fail");
        }
        return this;
    }

    protected void execute(SocketChannel channel, Callback0 callback0) {
        try {
            callback0.call();
        } catch (Exception e) {
            IoUtil.close(channel);
            throw new StreamServerException(e.getMessage());
        }
    }

    protected void execute(SocketChannel channel, Packet<Command> packet, Object defaultPayload,
                           Callback2<Object> callback) {
        Object newPayload = null;
        try {
            newPayload = callback.call();
        } catch (Exception e) {
            newPayload = defaultPayload;
            IoUtil.close(channel);
            throw new StreamServerException(e.getMessage());
        } finally {
            // 返回结果
            ChannelIo.writeIn(channel, packet.newPayload(newPayload));
        }
    }

    protected StreamEntryID publish(RedisClient client, String pipe, Map<String, String> hash) {
        return client.xadd(pipe, StreamEntryID.NEW_ENTRY, hash, Long.MAX_VALUE, true);
    }
}
