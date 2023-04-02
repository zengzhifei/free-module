package com.stoicfree.free.module.core.stream.handler;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import com.stoicfree.free.module.core.common.misc.socket.nio.ChannelIo;
import com.stoicfree.free.module.core.common.misc.socket.nio.protocol.Packet;
import com.stoicfree.free.module.core.redis.client.RedisClient;
import com.stoicfree.free.module.core.stream.Streamer;
import com.stoicfree.free.module.core.stream.exception.StreamServerException;
import com.stoicfree.free.module.core.stream.protocol.Command;
import com.stoicfree.free.module.core.stream.protocol.Payload;

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
        execute(channel, packet, false, () -> {
            Payload.Provider.Auth auth = packet.getPayload(Payload.Provider.Auth.class);

            // 验证pipe
            Boolean ret = client.hexists(Streamer.getPipeKey(auth.getPipe()), Streamer.safe(auth.getPassword()));
            if (!ret) {
                ChannelIo.writeIn(channel, packet.newPayload(false));
                throw new StreamServerException("provider auth fail");
            }

            // 种入验证标识
            selectionKey.attach(true);

            return true;
        });
    }
}
