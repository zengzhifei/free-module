package com.stoicfree.free.module.core.stream;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.stoicfree.free.module.core.common.enums.ErrorCode;
import com.stoicfree.free.module.core.common.support.Assert;
import com.stoicfree.free.module.core.redis.client.RedisClient;
import com.stoicfree.free.module.core.stream.config.ProviderProperties;
import com.stoicfree.free.module.core.stream.constant.StreamConstants;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.StreamEntryID;

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
    }

    public String publish(String message) {
        StreamEntryID id = null;
        try {
            Map<String, String> hash = new HashMap<>(1);
            hash.put(StreamConstants.HASH_KEY, message);
            id = client.xadd(properties.getPipe(), StreamEntryID.NEW_ENTRY, hash, properties.getMaxLen(), true);
            return id.toString();
        } finally {
            log.info("stream provider publish message[{}], messageId[{}]", message, id);
        }
    }

    private boolean auth() {
        return client.hexists(Streamer.getPipeKey(properties.getPipe()), Streamer.safe(properties.getPassword()));
    }
}
