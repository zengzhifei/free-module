package com.stoicfree.free.db.module.support;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 分表逻辑上下文
 */
public class ShardingThreadLocal {
    private static final ThreadLocal<Map<String, Long>> CURRENT_TABLE = new ThreadLocal<>();

    public static Long get(String tableName) {
        return Optional.ofNullable(CURRENT_TABLE.get()).map(e -> e.get(tableName)).orElse(null);
    }

    public static void set(String tableName, Long realTableIndex) {
        Map<String, Long> tableMap = Optional.ofNullable(CURRENT_TABLE.get()).orElse(new HashMap<>());
        tableMap.put(tableName, realTableIndex);
        CURRENT_TABLE.set(tableMap);
    }

    public static void clean() {
        CURRENT_TABLE.remove();
    }
}
