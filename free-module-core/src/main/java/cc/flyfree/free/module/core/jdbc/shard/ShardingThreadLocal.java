package cc.flyfree.free.module.core.jdbc.shard;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 分表逻辑
 *
 * @author zengzhifei
 * @date 2024/5/6 16:40
 */
public class ShardingThreadLocal {
    private static final ThreadLocal<Map<String, Object>> CURRENT_TABLE = new ThreadLocal<>();

    public static Object get(String tableName) {
        return Optional.ofNullable(CURRENT_TABLE.get()).map(e -> e.get(tableName)).orElse(null);
    }

    public static void set(String tableName, Object place) {
        Map<String, Object> tableMap = Optional.ofNullable(CURRENT_TABLE.get()).orElse(new HashMap<>());
        tableMap.put(tableName, place);
        CURRENT_TABLE.set(tableMap);
    }

    public static void clean() {
        CURRENT_TABLE.remove();
    }
}
