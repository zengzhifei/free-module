package com.stoicfree.free.module.core.stream.handler;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;

import com.stoicfree.free.module.core.common.gson.GsonUtil;
import com.stoicfree.free.module.core.common.misc.socket.nio.protocol.Packet;
import com.stoicfree.free.module.core.common.support.ExecutorHelper;
import com.stoicfree.free.module.core.common.util.DateUtils;
import com.stoicfree.free.module.core.redis.client.RedisClient;
import com.stoicfree.free.module.core.stream.Streamer;
import com.stoicfree.free.module.core.stream.protocol.Command;
import com.stoicfree.free.module.core.stream.protocol.Payload;

import cn.hutool.core.net.NetUtil;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.StreamEntryID;
import redis.clients.jedis.Tuple;

/**
 * @author zengzhifei
 * @date 2023/4/1 00:04
 */
@Slf4j
public class DelayQueueHandler extends BaseHandler {
    private static final ScheduledThreadPoolExecutor EXECUTOR = ExecutorHelper.newScheduledThreadPool(
            "stream-delay-consumer", 2);

    @Override
    public boolean match(Command command) {
        return false;
    }

    @Override
    public void handle(RedisClient client, SelectionKey selectionKey, SocketChannel channel, Packet<Command> packet) {

    }

    public void handle(RedisClient client) {
        // 校验和设置运行中delay queue消费
        if (client.hexists(Streamer.RUNNING_CONSUME_DELAY_QUEUE_KEY, "0")) {
            return;
        } else {
            client.hset(Streamer.RUNNING_CONSUME_DELAY_QUEUE_KEY, "0", NetUtil.getLocalhostStr());
        }

        // 延迟消费
        EXECUTOR.scheduleAtFixedRate(() -> readDelayQueue(client), 0, 1, TimeUnit.SECONDS);
    }

    private void readDelayQueue(RedisClient client) {
        List<String> members = new ArrayList<>();
        try {
            Set<Tuple> scoreAndMembers = client.zrangeByScoreWithScores(Streamer.DELAY_KEY, 0,
                    DateUtils.currentSeconds());
            for (Tuple scoreAndMember : scoreAndMembers) {
                String member = scoreAndMember.getElement();
                members.add(member);
                Payload.Provider.DelayQueue delayQueue = GsonUtil.fromJson(member, Payload.Provider.DelayQueue.class);
                publishDelayMessage(client, delayQueue);
            }
        } catch (Exception e) {
            log.error("provider read delay queue error", e);
        } finally {
            if (CollectionUtils.isNotEmpty(members)) {
                client.zrem(Streamer.DELAY_KEY, members.toArray(new String[0]));
            }
        }
    }

    private void publishDelayMessage(RedisClient client, Payload.Provider.DelayQueue delayQueue) {
        StreamEntryID id = null;
        try {
            Map<String, String> hash = new HashMap<>(2);
            hash.put(Streamer.HASH_KEY, delayQueue.getMessage());
            hash.put(Streamer.DELAY_ID, delayQueue.getId());
            id = publish(client, delayQueue.getPipe(), hash);
        } finally {
            log.info("provider publish delay member[{}], messageId[{}]", delayQueue, id);
        }
    }
}
