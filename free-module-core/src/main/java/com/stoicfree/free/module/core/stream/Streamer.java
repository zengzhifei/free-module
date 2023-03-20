package com.stoicfree.free.module.core.stream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.stoicfree.free.module.core.common.enums.ErrorCode;
import com.stoicfree.free.module.core.common.support.Assert;
import com.stoicfree.free.module.core.common.util.PrimitiveUtils;
import com.stoicfree.free.module.core.redis.client.RedisClient;
import com.stoicfree.free.module.core.stream.constant.StreamConstants;

import cn.hutool.core.comparator.VersionComparator;
import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.SecureUtil;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

/**
 * @author zengzhifei
 * @date 2023/3/19 18:15
 */
public class Streamer {
    private final RedisClient client;

    public Streamer(RedisClient client) {
        this.client = client;
    }

    public static String getPipeKey(String pipe) {
        return StreamConstants.PIPE_PREFIX + pipe;
    }

    public static String getQueueKey(String queue) {
        return StreamConstants.QUEUE_PREFIX + queue;
    }

    public static String safe(String data) {
        return SecureUtil.md5(data);
    }

    public static boolean checkVersion(String info) {
        Map<String, String> infoMap = PrimitiveUtils.strToMap(info, "\r\n", ":");
        String version = infoMap.getOrDefault("redis_version", "0.0.0");
        return VersionComparator.INSTANCE.compare(version, StreamConstants.VERSION) >= 0;
    }

    public boolean registerPipe(String pipe, String password) {
        Assert.notBlank(pipe, ErrorCode.INVALID_PARAMS);
        Assert.notBlank(password, ErrorCode.INVALID_PARAMS);

        return client.hsetnx(getPipeKey(pipe), safe(password), DateUtil.now()) > 0;
    }

    public boolean registerQueue(String pipe, String queue, String token) {
        Assert.notBlank(pipe, ErrorCode.INVALID_PARAMS);
        Assert.notBlank(queue, ErrorCode.INVALID_PARAMS);
        Assert.notBlank(token, ErrorCode.INVALID_PARAMS);
        Assert.isTrue(client.exists(getPipeKey(pipe)), ErrorCode.INVALID_PARAMS, "pipe不存在");

        return client.hsetnx(getQueueKey(queue), safe(token), pipe) > 0;
    }

    public Map<String, List<String>> info() {
        List<String> pipes = getPipes();
        Map<String, List<String>> queues = getQueues();

        Map<String, List<String>> pipeQueuesMap = new HashMap<>(pipes.size());
        for (String pipe : pipes) {
            pipeQueuesMap.put(pipe, queues.get(pipe));
        }

        return pipeQueuesMap;
    }

    private List<String> getPipes() {
        List<String> pipes = new ArrayList<>();
        String cursor = "0";
        ScanParams scanParams = new ScanParams();
        scanParams.match(StreamConstants.PIPE_PREFIX + "*");
        while (true) {
            ScanResult<String> result = client.scan(cursor, scanParams, "hash");
            pipes.addAll(result.getResult());
            if (result.isCompleteIteration()) {
                break;
            }
        }
        return pipes.stream().map(e -> e.substring(StreamConstants.PIPE_PREFIX.length())).collect(Collectors.toList());
    }

    private Map<String, List<String>> getQueues() {
        List<String> queues = new ArrayList<>();
        String cursor = "0";
        ScanParams scanParams = new ScanParams();
        scanParams.match(StreamConstants.QUEUE_PREFIX + "*");
        while (true) {
            ScanResult<String> result = client.scan(cursor, scanParams, "hash");
            queues.addAll(result.getResult());
            if (result.isCompleteIteration()) {
                break;
            }
        }

        Map<String, List<String>> pipeQueueMap = new HashMap<>(queues.size());
        for (String queue : queues) {
            Map<String, String> tokenPipe = client.hgetAll(queue);
            List<String> pipes = tokenPipe.values().stream().distinct().collect(Collectors.toList());
            queue = queue.substring(StreamConstants.QUEUE_PREFIX.length());
            for (String pipe : pipes) {
                List<String> pipeQueues = pipeQueueMap.getOrDefault(pipe, new ArrayList<>());
                pipeQueues.add(queue);
                pipeQueueMap.put(pipe, pipeQueues);
            }
        }
        return pipeQueueMap;
    }
}
