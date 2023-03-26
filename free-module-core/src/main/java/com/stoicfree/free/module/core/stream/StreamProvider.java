package com.stoicfree.free.module.core.stream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;

import com.stoicfree.free.module.core.common.enums.ErrorCode;
import com.stoicfree.free.module.core.common.gson.GsonUtil;
import com.stoicfree.free.module.core.common.support.Assert;
import com.stoicfree.free.module.core.common.support.ExecutorHelper;
import com.stoicfree.free.module.core.common.support.ID;
import com.stoicfree.free.module.core.common.util.DateUtils;
import com.stoicfree.free.module.core.redis.client.RedisClient;
import com.stoicfree.free.module.core.stream.config.ProviderProperties;
import com.stoicfree.free.module.core.stream.constant.StreamConstants;
import com.stoicfree.free.module.core.stream.domain.DelayMember;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.StreamEntryID;
import redis.clients.jedis.Tuple;

/**
 * @author zengzhifei
 * @date 2023/3/19 18:15
 */
@Slf4j
public class StreamProvider {
    private final RedisClient client;
    private final ProviderProperties properties;

    public StreamProvider(RedisClient client, ProviderProperties properties) {
        this.client = client;
        this.properties = properties;
    }

    @PostConstruct
    private void init() {
        // 参数校验
        Assert.notNull(client, ErrorCode.INVALID_PARAMS, "redis client not be null");
        Assert.allFieldsValid(properties, ErrorCode.INVALID_PARAMS, "%s must be valid", "properties not be null");

        // 版本校验
        Assert.isTrue(Streamer.checkVersion(client.info("Server")), ErrorCode.VERSION_ERROR,
                "redis server version must more than " + StreamConstants.VERSION);

        // 权限校验
        Assert.isTrue(auth(), ErrorCode.AUTH_FAIL);

        // 延迟队列
        readDelayQueue();
    }

    public String publish(String message) {
        StreamEntryID id = null;
        try {
            Map<String, String> hash = new HashMap<>(1);
            hash.put(StreamConstants.HASH_KEY, message);
            id = publishMessage(hash);
            return id.toString();
        } finally {
            log.info("stream provider publish message[{}], messageId[{}]", message, id);
        }
    }

    public String delayPublish(String message, long time) {
        String id = ID.SNOWFLAKE.nextIdStr();
        try {
            DelayMember member = DelayMember.builder().id(id).message(message).build();
            boolean ret = client.zadd(StreamConstants.DELAY_KEY, time, GsonUtil.toJson(member)) > 0;
            if (!ret) {
                throw new RuntimeException("delay publish fail");
            }
            return id;
        } finally {
            log.info("stream provider delay publish message[{}], time[{}], messageId[{}]", message, time, id);
        }
    }

    private void readDelayQueue() {
        ScheduledThreadPoolExecutor executor = ExecutorHelper.newScheduledThreadPool("stream-delay-consumer", 2);
        executor.scheduleAtFixedRate(() -> {
            List<String> members = new ArrayList<>();
            try {
                Set<Tuple> scoreAndMembers = client.zrangeByScoreWithScores(StreamConstants.DELAY_KEY, 0,
                        DateUtils.current(), 0, 1);
                for (Tuple scoreAndMember : scoreAndMembers) {
                    String member = scoreAndMember.getElement();
                    members.add(member);
                    DelayMember delayMember = GsonUtil.fromJson(member, DelayMember.class);
                    publishDelayMessage(delayMember);
                }
            } catch (Exception e) {
                log.error("stream provider read delay queue error", e);
            } finally {
                if (CollectionUtils.isNotEmpty(members)) {
                    client.zrem(StreamConstants.DELAY_KEY, members.toArray(new String[0]));
                }
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    private void publishDelayMessage(DelayMember delayMember) {
        StreamEntryID id = null;
        try {
            Map<String, String> hash = new HashMap<>(2);
            hash.put(StreamConstants.HASH_KEY, delayMember.getMessage());
            hash.put(StreamConstants.DELAY_ID, delayMember.getId());
            id = publishMessage(hash);
        } finally {
            log.info("stream provider publish delay member[{}], messageId[{}]", delayMember, id);
        }
    }

    private StreamEntryID publishMessage(Map<String, String> hash) {
        return client.xadd(properties.getPipe(), StreamEntryID.NEW_ENTRY, hash, properties.getMaxLen(), true);
    }

    private boolean auth() {
        return client.hexists(Streamer.getPipeKey(properties.getPipe()), Streamer.safe(properties.getPassword()));
    }
}
