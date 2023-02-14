package com.stoicfree.free.redis.module.client;

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
}
