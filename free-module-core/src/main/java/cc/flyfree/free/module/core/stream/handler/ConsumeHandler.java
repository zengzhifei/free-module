package cc.flyfree.free.module.core.stream.handler;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import cc.flyfree.free.module.core.common.constant.Constants;
import cc.flyfree.free.module.core.common.misc.socket.nio.ChannelIo;
import cc.flyfree.free.module.core.common.misc.socket.nio.protocol.Packet;
import cc.flyfree.free.module.core.common.support.ExecutorHelper;
import cc.flyfree.free.module.core.redis.client.RedisClient;
import cc.flyfree.free.module.core.stream.Streamer;
import cc.flyfree.free.module.core.stream.domain.Message;
import cc.flyfree.free.module.core.stream.protocol.Command;
import cc.flyfree.free.module.core.stream.protocol.Payload;

import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.RuntimeUtil;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.StreamEntry;
import redis.clients.jedis.StreamEntryID;
import redis.clients.jedis.params.XReadGroupParams;

/**
 * @author zengzhifei
 * @date 2023/4/1 10:29
 */
@Slf4j
public class ConsumeHandler extends BaseHandler {
    private static final ExecutorService EXECUTOR = ExecutorHelper.newFixedThreadPool("stream-consumer", 2, 4);

    @Override
    public boolean match(Command command) {
        return Command.CONSUME.equals(command);
    }

    @Override
    public void handle(RedisClient client, SelectionKey selectionKey, SocketChannel channel, Packet<Command> packet) {
        execute(channel, () -> consume(client, channel, packet));
    }

    private void consume(RedisClient client, SocketChannel channel, Packet<Command> packet) {
        Payload.Consumer.Consume consume = packet.getPayload(Payload.Consumer.Consume.class);
        String pipe = consume.getPipe();
        String queue = consume.getQueue();

        // 获取queue消费锁
        String ip = NetUtil.getLocalhostStr();
        String lock = client.lock(Streamer.getRunningConsumeQueueKey(queue), ip, 1000L * Constants.YEAR);
        if (!RedisClient.OK.equals(lock)) {
            log.info("stream consume queue[{}] is running", queue);
            return;
        } else {
            // 服务关闭,解锁
            RuntimeUtil.addShutdownHook(
                    () -> client.unlock(Streamer.getRunningConsumeQueueKey(queue), ip));
        }

        // 线程池消费
        EXECUTOR.execute(() -> {
            XReadGroupParams groupParams = XReadGroupParams.xReadGroupParams().block(0).count(consume.getCount());
            Map<String, StreamEntryID> streams = new HashMap<>(1);
            streams.put(Streamer.getStreamKey(pipe), StreamEntryID.UNRECEIVED_ENTRY);

            while (true) {
                List<Map.Entry<String, List<StreamEntry>>> entries = null;
                try {
                    entries = client.xreadGroup(queue, Streamer.DEFAULT_CONSUMER_ID, groupParams, streams);
                    List<StreamEntryID> ids = new ArrayList<>();
                    List<Message> messages = new ArrayList<>();
                    for (StreamEntry entry : entries.get(0).getValue()) {
                        ids.add(entry.getID());
                        Map<String, String> fields = entry.getFields();
                        Message message = Message.builder()
                                .messageId(entry.getID().toString())
                                .delayId(fields.get(Streamer.DELAY_ID))
                                .message(fields.get(Streamer.HASH_KEY))
                                .build();
                        messages.add(message);
                    }

                    // 返回消息
                    ChannelIo.writeIn(channel, packet.newPayload(messages));

                    // ack
                    client.xack(pipe, queue, ids.toArray(new StreamEntryID[0]));
                } catch (Exception e) {
                    log.error("stream consumer consume", e);
                } finally {
                    log.info("stream consumer consume pipe[{}], queue[{}], hash[{}]", pipe, queue, entries);
                }
            }
        });
    }
}
