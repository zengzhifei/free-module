package cc.flyfree.free.module.core.stream.handler;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

import cc.flyfree.free.module.core.common.misc.socket.nio.protocol.Packet;
import cc.flyfree.free.module.core.redis.client.RedisClient;
import cc.flyfree.free.module.core.stream.Streamer;
import cc.flyfree.free.module.core.stream.protocol.Command;
import cc.flyfree.free.module.core.stream.protocol.Payload;

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
        execute(channel, packet, null, () -> {
            Payload.Provider.Publish publish = packet.getPayload(Payload.Provider.Publish.class);

            // 发布消息
            Map<String, String> hash = new HashMap<>(1);
            hash.put(Streamer.HASH_KEY, publish.getMessage());

            return publish(client, publish.getPipe(), hash).toString();
        });
    }
}
