package cc.flyfree.free.module.core.stream.handler;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.apache.commons.lang.StringUtils;

import cc.flyfree.free.module.core.common.misc.socket.nio.protocol.Packet;
import cc.flyfree.free.module.core.common.support.ExecutorHelper;
import cc.flyfree.free.module.core.common.support.Safes;
import cc.flyfree.free.module.core.redis.client.RedisClient;
import cc.flyfree.free.module.core.stream.Streamer;
import cc.flyfree.free.module.core.stream.exception.StreamServerException;
import cc.flyfree.free.module.core.stream.protocol.Command;
import cc.flyfree.free.module.core.stream.protocol.Payload;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.StreamEntryID;
import redis.clients.jedis.StreamGroupInfo;

/**
 * @author zengzhifei
 * @date 2023/4/1 10:29
 */
@Slf4j
public class ConsumerAuthHandler extends BaseHandler {
    private static final ExecutorService EXECUTOR = ExecutorHelper.newFixedThreadPool("stream-consumer", 2, 4);

    @Override
    public boolean match(Command command) {
        return Command.CONSUMER_AUTH.equals(command);
    }

    @Override
    public CommandHandler validate(SelectionKey selectionKey, SocketChannel channel) {
        return this;
    }

    @Override
    public void handle(RedisClient client, SelectionKey selectionKey, SocketChannel channel, Packet<Command> packet) {
        execute(channel, packet, null, () -> {
            Payload.Consumer.Auth auth = packet.getPayload(Payload.Consumer.Auth.class);

            // 验证queue
            String pipe = client.hget(Streamer.getQueueKey(auth.getQueue()), Streamer.safe(auth.getToken()));
            if (StringUtils.isBlank(pipe)) {
                throw new StreamServerException("consumer auth fail");
            }

            // 创建分组
            if (!createGroup(client, pipe, auth.getQueue())) {
                throw new StreamServerException("consumer create group fail");
            }

            // 种入验证标识
            selectionKey.attach(true);

            return pipe;
        });
    }

    private boolean createGroup(RedisClient client, String pipe, String queue) {
        if (client.exists(Streamer.getStreamKey(pipe))) {
            List<StreamGroupInfo> groupInfos = client.xinfoGroup(Streamer.getStreamKey(pipe));
            boolean isCreated = Safes.of(groupInfos).stream().anyMatch(e -> e.getName().equals(queue));
            if (isCreated) {
                return true;
            }
        }

        String ok = client.xgroupCreate(Streamer.getStreamKey(pipe), queue, StreamEntryID.LAST_ENTRY, true);
        return RedisClient.OK.equals(ok);
    }
}
