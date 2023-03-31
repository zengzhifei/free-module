package com.stoicfree.free.module.core.stream.handler;

import java.nio.channels.SocketChannel;

import com.stoicfree.free.module.core.redis.client.RedisClient;
import com.stoicfree.free.module.core.stream.enums.Command;
import com.stoicfree.free.module.core.stream.protocol.Packet;

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
     * 执行命令
     *
     * @param client
     * @param channel
     * @param packet
     */
    void handle(RedisClient client, SocketChannel channel, Packet packet);
}
