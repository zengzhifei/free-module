package com.stoicfree.free.module.core.stream.handler;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

import com.stoicfree.free.module.core.common.misc.socket.nio.ChannelIo;
import com.stoicfree.free.module.core.common.misc.socket.nio.protocol.Packet;
import com.stoicfree.free.module.core.redis.client.RedisClient;
import com.stoicfree.free.module.core.stream.Streamer;
import com.stoicfree.free.module.core.stream.exception.StreamException;
import com.stoicfree.free.module.core.stream.protocol.Command;
import com.stoicfree.free.module.core.stream.protocol.Payload;

import redis.clients.jedis.StreamEntryID;

/**
 * @author zengzhifei
 * @date 2023/3/31 23:23
 */
public class PublishHandler extends BaseHandler {
    @Override
    public boolean match(Command command) {
        return Command.PUBLISH.equals(command);
    }

    @Override
    public void handle(RedisClient client, SelectionKey selectionKey, SocketChannel channel, Packet<Command> packet) {
        try {
            Payload.Provider.Publish publish = packet.getPayload(Payload.Provider.Publish.class);

            // 发布消息
            Map<String, String> hash = new HashMap<>(1);
            hash.put(Streamer.HASH_KEY, publish.getMessage());
            StreamEntryID id = publish(client, publish.getPipe(), hash);

            // 返回消息id
            ChannelIo.writeIn(channel, packet.newPayload(id.toString()));
        } catch (Exception e) {
            throw new StreamException(e.getMessage());
        }
    }
}
