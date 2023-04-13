package cc.flyfree.free.module.core.stream.handler;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import cc.flyfree.free.module.core.common.misc.socket.nio.protocol.Packet;
import cc.flyfree.free.module.core.redis.client.RedisClient;
import cc.flyfree.free.module.core.stream.protocol.Command;

/**
 * @author zengzhifei
 * @date 2023/3/31 22:54
 */
public interface CommandHandler {
    /**
     * 匹配执行器
     *
     * @param command
     *
     * @return
     */
    boolean match(Command command);

    /**
     * 验证命令
     *
     * @param selectionKey
     * @param channel
     *
     * @return
     */
    CommandHandler validate(SelectionKey selectionKey, SocketChannel channel);

    /**
     * 执行命令
     *
     * @param client
     * @param selectionKey
     * @param channel
     * @param packet
     */
    void handle(RedisClient client, SelectionKey selectionKey, SocketChannel channel, Packet<Command> packet);
}
