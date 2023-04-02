package com.stoicfree.free.module.core.stream.handler;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Map;

import com.stoicfree.free.module.core.common.misc.socket.nio.ChannelIo;
import com.stoicfree.free.module.core.common.misc.socket.nio.protocol.Packet;
import com.stoicfree.free.module.core.common.support.Callback0;
import com.stoicfree.free.module.core.common.support.Callback2;
import com.stoicfree.free.module.core.redis.client.RedisClient;
import com.stoicfree.free.module.core.stream.Streamer;
import com.stoicfree.free.module.core.stream.exception.StreamServerException;
import com.stoicfree.free.module.core.stream.protocol.Command;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.IoUtil;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.StreamEntryID;

/**
 * @author zengzhifei
 * @date 2023/3/31 23:51
 */
@Slf4j
public abstract class BaseHandler implements CommandHandler {
    @Override
    public CommandHandler validate(SelectionKey selectionKey, SocketChannel channel) {
        boolean isAuth = Convert.toBool(selectionKey.attachment(), false);
        if (!isAuth) {
            log.error("stream execute auth fail");
            IoUtil.close(channel);
            throw new StreamServerException("stream auth fail");
        }
        return this;
    }

    protected void execute(SocketChannel channel, Callback0 callback0) {
        try {
            callback0.call();
        } catch (Exception e) {
            log.error("stream execute", e);
            IoUtil.close(channel);
            throw new StreamServerException(e.getMessage());
        }
    }

    protected void execute(SocketChannel channel, Packet<Command> packet, Object defaultPayload,
                           Callback2<Object> callback) {
        try {
            Object newPayload = callback.call();
            ChannelIo.writeIn(channel, packet.newPayload(newPayload));
        } catch (Exception e) {
            log.error("stream execute", e);
            ChannelIo.writeIn(channel, packet.newPayload(defaultPayload));
            IoUtil.close(channel);
            throw new StreamServerException(e.getMessage());
        } finally {
            log.info("stream write packet: {}", packet);
        }
    }

    protected StreamEntryID publish(RedisClient client, String pipe, Map<String, String> hash) {
        return client.xadd(Streamer.getStreamKey(pipe), StreamEntryID.NEW_ENTRY, hash, Long.MAX_VALUE, true);
    }
}
