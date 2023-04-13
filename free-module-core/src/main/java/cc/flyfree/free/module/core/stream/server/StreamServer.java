package cc.flyfree.free.module.core.stream.server;

import cc.flyfree.free.module.core.common.enums.ErrorCode;
import cc.flyfree.free.module.core.common.misc.socket.nio.ChannelHandler;
import cc.flyfree.free.module.core.common.misc.socket.nio.ChannelIo;
import cc.flyfree.free.module.core.common.misc.socket.nio.NioServer;
import cc.flyfree.free.module.core.common.misc.socket.nio.protocol.Packet;
import cc.flyfree.free.module.core.common.support.Assert;
import cc.flyfree.free.module.core.redis.client.RedisClient;
import cc.flyfree.free.module.core.stream.Streamer;
import cc.flyfree.free.module.core.stream.handler.CommandHandlerSelect;
import cc.flyfree.free.module.core.stream.handler.DelayQueueHandler;
import cc.flyfree.free.module.core.stream.protocol.Command;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zengzhifei
 * @date 2023/3/31 18:24
 */
@Slf4j
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
            log.info("stream read packet: {}", packet);
            CommandHandlerSelect.select(packet.getCommand()).validate(selectionKey, channel)
                    .handle(client, selectionKey, channel, packet);
        };
    }

    private void afterHandle() {
        new DelayQueueHandler().handle(client);
    }
}
