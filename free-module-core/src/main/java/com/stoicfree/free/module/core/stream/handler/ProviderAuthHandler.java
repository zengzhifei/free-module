package com.stoicfree.free.module.core.stream.handler;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import com.stoicfree.free.module.core.common.misc.socket.nio.ChannelIo;
import com.stoicfree.free.module.core.common.misc.socket.nio.protocol.Packet;
import com.stoicfree.free.module.core.redis.client.RedisClient;
import com.stoicfree.free.module.core.stream.Streamer;
import com.stoicfree.free.module.core.stream.exception.StreamException;
import com.stoicfree.free.module.core.stream.protocol.Command;
import com.stoicfree.free.module.core.stream.protocol.Payload;

import cn.hutool.core.io.IoUtil;

/**
 * @author zengzhifei
 * @date 2023/3/31 22:55
 */
public class ProviderAuthHandler extends BaseHandler {
    @Override
    public boolean match(Command command) {
        return Command.PROVIDER_AUTH.equals(command);
    }

    @Override
    public CommandHandler validate(SelectionKey selectionKey, SocketChannel channel) {
        return this;
    }

    @Override
    public void handle(RedisClient client, SelectionKey selectionKey, SocketChannel channel, Packet<Command> packet) {
        try {
            Payload.Provider.Auth auth = packet.getPayload(Payload.Provider.Auth.class);

            // 验证pipe
            Boolean ret = client.hexists(Streamer.getPipeKey(auth.getPipe()), Streamer.safe(auth.getPassword()));
            if (!ret) {
                throw new StreamException("provider auth fail");
            }

            // 种入验证标识
            selectionKey.attach(true);

            // 返回结果
            ChannelIo.writeIn(channel, packet.newPayload(true));
        } catch (Exception e) {
            IoUtil.close(channel);
            throw new StreamException(e.getMessage());
        }
    }
}
