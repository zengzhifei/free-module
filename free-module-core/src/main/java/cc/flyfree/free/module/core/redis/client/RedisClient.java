package cc.flyfree.free.module.core.redis.client;

import redis.clients.jedis.commands.AdvancedBinaryJedisCommands;
import redis.clients.jedis.commands.AdvancedJedisCommands;
import redis.clients.jedis.commands.BasicCommands;
import redis.clients.jedis.commands.BinaryJedisCommands;
import redis.clients.jedis.commands.BinaryScriptingCommands;
import redis.clients.jedis.commands.ClusterCommands;
import redis.clients.jedis.commands.JedisCommands;
import redis.clients.jedis.commands.ModuleCommands;
import redis.clients.jedis.commands.MultiKeyBinaryCommands;
import redis.clients.jedis.commands.MultiKeyCommands;
import redis.clients.jedis.commands.ScriptingCommands;
import redis.clients.jedis.commands.SentinelCommands;

/**
 * @author zengzhifei
 * @date 2023/2/14 15:23
 */
public interface RedisClient extends BasicCommands, BinaryJedisCommands, MultiKeyBinaryCommands,
        AdvancedBinaryJedisCommands, BinaryScriptingCommands, JedisCommands, MultiKeyCommands,
        AdvancedJedisCommands, ScriptingCommands, ClusterCommands, SentinelCommands, ModuleCommands {

    String OK = "OK";

    /**
     * 全局锁
     *
     * @param lockKey
     * @param requestId
     * @param milliSeconds
     *
     * @return
     */
    String lock(String lockKey, String requestId, long milliSeconds);

    /**
     * 释放全局锁
     *
     * @param lockKey
     * @param requestId
     *
     * @return
     */
    Object unlock(String lockKey, String requestId);
}
