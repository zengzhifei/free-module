package com.stoicfree.free.module.core.stream.handler;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.apache.commons.lang.StringUtils;

import com.stoicfree.free.module.core.common.gson.GsonUtil;
import com.stoicfree.free.module.core.common.misc.socket.nio.ChannelHelper;
import com.stoicfree.free.module.core.common.misc.socket.nio.protocol.Packet;
import com.stoicfree.free.module.core.common.support.ExecutorHelper;
import com.stoicfree.free.module.core.common.support.Safes;
import com.stoicfree.free.module.core.redis.client.RedisClient;
import com.stoicfree.free.module.core.stream.Streamer;
import com.stoicfree.free.module.core.stream.domain.Message;
import com.stoicfree.free.module.core.stream.exception.StreamException;
import com.stoicfree.free.module.core.stream.protocol.Command;
import com.stoicfree.free.module.core.stream.protocol.Payload;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.net.NetUtil;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.StreamEntry;
import redis.clients.jedis.StreamEntryID;
import redis.clients.jedis.StreamGroupInfo;
import redis.clients.jedis.params.XReadGroupParams;

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
        try {
            Payload.Consumer.Auth auth = packet.getPayload(Payload.Consumer.Auth.class);

            String pipe = client.hget(Streamer.getQueueKey(auth.getQueue()), Streamer.safe(auth.getToken()));
            if (StringUtils.isBlank(pipe)) {
                throw new StreamException("consumer auth fail");
            }
            if (!createGroup(client, pipe, auth.getQueue())) {
                throw new StreamException("consumer create group fail");
            }

            selectionKey.attach(true);

            consume(client, channel, pipe, auth.getQueue(), auth.getCount());
        } catch (Exception e) {
            IoUtil.close(channel);
            throw new StreamException(e.getMessage());
        }
    }

    private boolean createGroup(RedisClient client, String pipe, String queue) {
        if (client.exists(pipe)) {
            List<StreamGroupInfo> groupInfos = client.xinfoGroup(pipe);
            boolean isCreated = Safes.of(groupInfos).stream().anyMatch(e -> e.getName().equals(queue));
            if (isCreated) {
                return true;
            }
        }

        String ok = client.xgroupCreate(pipe, queue, StreamEntryID.LAST_ENTRY, true);
        return RedisClient.OK.equals(ok);
    }

    private void consume(RedisClient client, SocketChannel channel, String pipe, String queue, int count) {
        if (client.hexists(Streamer.RUNNING_CONSUME_QUEUE_KEY, queue)) {
            return;
        } else {
            client.hset(Streamer.RUNNING_CONSUME_QUEUE_KEY, queue, NetUtil.getLocalhostStr());
        }

        EXECUTOR.execute(() -> {
            XReadGroupParams groupParams = XReadGroupParams.xReadGroupParams().block(0).count(count);
            Map<String, StreamEntryID> streams = new HashMap<>(1);
            streams.put(pipe, StreamEntryID.UNRECEIVED_ENTRY);

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

                    ChannelHelper.write(channel, GsonUtil.toJson(messages));

                    client.xack(pipe, queue, ids.toArray(new StreamEntryID[0]));
                } catch (Exception e) {
                    log.error("stream consumer consume", e);
                } finally {
                    log.info("stream consumer consume pipe[{}], queue[{}], hash[{}]",
                            pipe, queue, GsonUtil.toJson(entries));
                }
            }
        });
    }
}
