package com.stoicfree.free.module.core.stream.server;

import com.stoicfree.free.module.core.common.enums.ErrorCode;
import com.stoicfree.free.module.core.common.misc.socket.nio.ChannelHandler;
import com.stoicfree.free.module.core.common.misc.socket.nio.ChannelIo;
import com.stoicfree.free.module.core.common.misc.socket.nio.NioServer;
import com.stoicfree.free.module.core.common.misc.socket.nio.protocol.Packet;
import com.stoicfree.free.module.core.common.support.Assert;
import com.stoicfree.free.module.core.redis.client.RedisClient;
import com.stoicfree.free.module.core.stream.Streamer;
import com.stoicfree.free.module.core.stream.handler.CommandHandlerSelect;
import com.stoicfree.free.module.core.stream.handler.DelayQueueHandler;
import com.stoicfree.free.module.core.stream.protocol.Command;

/**
 * @author zengzhifei
 * @date 2023/3/31 18:24
 */
public class StreamServer {
    private final RedisClient client;

    public StreamServer(RedisClient client, int port) {
        // 版本校验
        Assert.isTrue(Streamer.checkVersion(client.info("Server")), ErrorCode.VERSION_ERROR,
                "redis server version must more than " + Streamer.VERSION);

        // 设置数据源
        this.client = client;

        // 启动服务
        NioServer server = new NioServer(port);
        server.registerChannelHandler(getChannelHandler()).start();

        // 执行后续事件
        this.afterHandle();
    }

    private ChannelHandler getChannelHandler() {
        return (selectionKey, channel) -> {
            Packet<Command> packet = ChannelIo.readout(channel);
            CommandHandlerSelect.select(packet.getCommand()).validate(selectionKey, channel)
                    .handle(client, selectionKey, channel, packet);
        };
    }

    private void afterHandle() {
        new DelayQueueHandler().handle(client);
    }
}
