package cc.flyfree.free.module.core.stream;

import java.util.Map;

import cc.flyfree.free.module.core.common.util.BaseUtils;

import cn.hutool.core.comparator.VersionComparator;
import cn.hutool.crypto.SecureUtil;

/**
 * @author zengzhifei
 * @date 2023/3/19 18:15
 */
public class Streamer {
    public static final String HASH_KEY = "message";
    public static final String DELAY_ID = "delayId";
    public static final String PIPE_PREFIX = "pipe:";
    public static final String QUEUE_PREFIX = "queue:";
    public static final String DELAY_PREFIX = "delay:";
    public static final String STREAM_PREFIX = "stream:";
    public static final String RUNNING_CONSUME_QUEUE_PREFIX = "running:consume_queue:";
    public static final String RUNNING_CONSUME_DELAY_PREFIX = "running:consume_delay:";
    public static final String DEFAULT_CONSUMER_ID = "c0";
    public static final String VERSION = "5.0";

    public static String getPipeKey(String pipe) {
        return PIPE_PREFIX + pipe;
    }

    public static String getQueueKey(String queue) {
        return QUEUE_PREFIX + queue;
    }

    public static String getDelayKey(int index) {
        return DELAY_PREFIX + index;
    }

    public static String getStreamKey(String pipe) {
        return STREAM_PREFIX + pipe;
    }

    public static String getRunningConsumeQueueKey(String queue) {
        return RUNNING_CONSUME_QUEUE_PREFIX + queue;
    }

    public static String getRunningConsumeDelayKey(int index) {
        return RUNNING_CONSUME_DELAY_PREFIX + index;
    }

    public static String safe(String data) {
        return SecureUtil.md5(data);
    }

    public static boolean checkVersion(String info) {
        Map<String, String> infoMap = BaseUtils.strToMap(info, "\r\n", ":");
        String version = infoMap.getOrDefault("redis_version", "0.0.0");
        return VersionComparator.INSTANCE.compare(version, VERSION) >= 0;
    }
}
