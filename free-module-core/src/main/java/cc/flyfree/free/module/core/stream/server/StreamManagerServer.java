package cc.flyfree.free.module.core.stream.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cc.flyfree.free.module.core.common.enums.ErrorCode;
import cc.flyfree.free.module.core.common.support.Assert;
import cc.flyfree.free.module.core.redis.client.RedisClient;
import cc.flyfree.free.module.core.stream.Streamer;

import cn.hutool.core.date.DateUtil;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

/**
 * @author zengzhifei
 * @date 2023/3/31 18:24
 */
public class StreamManagerServer {
    private final RedisClient client;

    public StreamManagerServer(RedisClient client) {
        this.client = client;
    }

    public boolean registerPipe(String pipe, String password) {
        Assert.notBlank(pipe, ErrorCode.INVALID_PARAMS);
        Assert.notBlank(password, ErrorCode.INVALID_PARAMS);

        return client.hsetnx(Streamer.getPipeKey(pipe), Streamer.safe(password), DateUtil.now()) > 0;
    }

    public boolean registerQueue(String pipe, String queue, String token) {
        Assert.notBlank(pipe, ErrorCode.INVALID_PARAMS);
        Assert.notBlank(queue, ErrorCode.INVALID_PARAMS);
        Assert.notBlank(token, ErrorCode.INVALID_PARAMS);
        Assert.isTrue(client.exists(Streamer.getPipeKey(pipe)), ErrorCode.INVALID_PARAMS, "pipe不存在");

        return client.hsetnx(Streamer.getQueueKey(queue), Streamer.safe(token), pipe) > 0;
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
        scanParams.match(Streamer.PIPE_PREFIX + "*");
        while (true) {
            ScanResult<String> result = client.scan(cursor, scanParams, "hash");
            pipes.addAll(result.getResult());
            if (result.isCompleteIteration()) {
                break;
            }
        }
        return pipes.stream().map(e -> e.substring(Streamer.PIPE_PREFIX.length())).collect(Collectors.toList());
    }

    private Map<String, List<String>> getQueues() {
        List<String> queues = new ArrayList<>();
        String cursor = "0";
        ScanParams scanParams = new ScanParams();
        scanParams.match(Streamer.QUEUE_PREFIX + "*");
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
            queue = queue.substring(Streamer.QUEUE_PREFIX.length());
            for (String pipe : pipes) {
                List<String> pipeQueues = pipeQueueMap.getOrDefault(pipe, new ArrayList<>());
                pipeQueues.add(queue);
                pipeQueueMap.put(pipe, pipeQueues);
            }
        }
        return pipeQueueMap;
    }
}
