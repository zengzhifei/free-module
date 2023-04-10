package com.stoicfree.free.module.core.jdbc.sharding;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 分表逻辑上下文
 *
 * @author zengzhifei
 */
public class ShardingThreadLocal {
    private static final ThreadLocal<Map<String, String>> CURRENT_TABLE = new ThreadLocal<>();

    public static String get(String tableName) {
        return Optional.ofNullable(CURRENT_TABLE.get()).map(e -> e.get(tableName)).orElse(null);
    }

    public static void set(String tableName, String realTableIndex) {
        Map<String, String> tableMap = Optional.ofNullable(CURRENT_TABLE.get()).orElse(new HashMap<>());
        tableMap.put(tableName, realTableIndex);
        CURRENT_TABLE.set(tableMap);
    }

    public static void remove() {
        CURRENT_TABLE.remove();
    }
}
