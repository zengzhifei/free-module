package com.stoicfree.free.module.core.stream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;

import com.stoicfree.free.module.core.common.enums.ErrorCode;
import com.stoicfree.free.module.core.common.gson.GsonUtil;
import com.stoicfree.free.module.core.common.support.Assert;
import com.stoicfree.free.module.core.common.support.ExecutorHelper;
import com.stoicfree.free.module.core.common.support.Safes;
import com.stoicfree.free.module.core.redis.client.RedisClient;
import com.stoicfree.free.module.core.stream.config.ConsumerProperties;
import com.stoicfree.free.module.core.stream.constant.StreamConstants;
import com.stoicfree.free.module.core.stream.domain.Message;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.StreamEntry;
import redis.clients.jedis.StreamEntryID;
import redis.clients.jedis.StreamGroupInfo;
import redis.clients.jedis.params.XReadGroupParams;

/**
 * @author zengzhifei
 * @date 2023/3/19 18:15
 */
@Slf4j
public class StreamConsumer {
    private final RedisClient client;
    private final ConsumerProperties properties;
    private final IConsumer consumer;
    private String pipe = null;

    private static final ExecutorService EXECUTOR = ExecutorHelper.newFixedThreadPool("stream-consumer", 2, 4);

    public StreamConsumer(RedisClient client, ConsumerProperties properties, IConsumer consumer) {
        this.client = client;
        this.properties = properties;
        this.consumer = consumer;
    }

    @PostConstruct
    private void init() {
        // 参数校验
        Assert.notNull(client, ErrorCode.INVALID_PARAMS, "redis client not be null");
        Assert.allFieldsValid(properties, ErrorCode.INVALID_PARAMS, "%s must be valid", "properties not be null");
        Assert.notNull(consumer, ErrorCode.INVALID_PARAMS, "consumer not be null");

        // 校验权限
        Assert.isTrue(auth(), ErrorCode.AUTH_FAIL);

        // 版本校验
        Assert.isTrue(Streamer.checkVersion(client.info("Server")), ErrorCode.VERSION_ERROR,
                "redis server version must more than " + StreamConstants.VERSION);

        // 创建分组
        Assert.isTrue(createGroup(), ErrorCode.GROUP_FAIL);

        // 开始消费
        consume();
    }

    private void consume() {
        EXECUTOR.execute(() -> {
            XReadGroupParams groupParams = XReadGroupParams.xReadGroupParams().block(0).count(properties.getCount());
            Map<String, StreamEntryID> streams = new HashMap<>(1);
            streams.put(pipe, StreamEntryID.UNRECEIVED_ENTRY);
            String queue = properties.getQueue();

            while (true) {
                List<Map.Entry<String, List<StreamEntry>>> entries = null;
                try {
                    entries = client.xreadGroup(queue, StreamConstants.DEFAULT_CONSUMER_ID, groupParams, streams);
                    List<StreamEntryID> ids = new ArrayList<>();
                    List<Message> messages = new ArrayList<>();
                    for (StreamEntry entry : entries.get(0).getValue()) {
                        ids.add(entry.getID());
                        Message message = Message.builder().messageId(entry.getID().toString())
                                .message(entry.getFields().get(StreamConstants.HASH_KEY))
                                .build();
                        messages.add(message);
                    }
                    consumer.batchConsume(messages);
                    client.xack(pipe, queue, ids.toArray(new StreamEntryID[0]));
                } catch (Exception e) {
                    log.error("stream consumer consume", e);
                } finally {
                    log.info("stream consumer consume pipe[{}], queue[{}], hash[{}]", pipe, queue,
                            GsonUtil.toJson(entries));
                }
            }
        });
    }

    private boolean createGroup() {
        if (client.exists(pipe)) {
            List<StreamGroupInfo> groupInfos = client.xinfoGroup(pipe);
            boolean isCreated = Safes.of(groupInfos).stream().anyMatch(e -> e.getName().equals(properties.getQueue()));
            if (isCreated) {
                return true;
            }
        }

        String ok = client.xgroupCreate(pipe, properties.getQueue(), StreamEntryID.LAST_ENTRY, true);
        return RedisClient.OK.equals(ok);
    }

    private boolean auth() {
        pipe = client.hget(Streamer.getQueueKey(properties.getQueue()), Streamer.safe(properties.getToken()));
        return StringUtils.isNotBlank(pipe);
    }
}
