package cc.flyfree.free.module.core.jdbc.shard;

import java.lang.reflect.Field;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.baomidou.mybatisplus.annotation.TableName;
import com.google.common.collect.Maps;

import cc.flyfree.free.module.core.common.enums.ErrorCode;
import cc.flyfree.free.module.core.common.support.Assert;
import cc.flyfree.free.module.core.common.support.Callback2;

/**
 * @author zengzhifei
 * @date 2024/5/6 20:29
 */
public class Sharder<E> {
    private final Map<Class<E>, Object> entityClassShardValueMap;

    private Sharder(Map<Class<E>, Object> entityClassShardValueMap) {
        this.entityClassShardValueMap = entityClassShardValueMap;
    }

    public static <E> Sharder<E> shard(Class<E> entityClass, Object shardValue) {
        Map<Class<E>, Object> entityClassModValueMap = Maps.newHashMap();
        entityClassModValueMap.put(entityClass, shardValue);
        return shard(entityClassModValueMap);
    }

    public static <E> Sharder<E> shard(Map<Class<E>, Object> entityClassShardValueMap) {
        return new Sharder<>(entityClassShardValueMap);
    }

    public <R> R execute(Callback2<R> callback) {
        try {
            sharding();
            return callback.call();
        } finally {
            clean();
        }
    }

    private void sharding() {
        for (Map.Entry<Class<E>, Object> entry : entityClassShardValueMap.entrySet()) {
            sharding(entry.getKey(), entry.getValue());
        }
    }

    private void sharding(Class<E> entityClass, Object value) {
        Assert.notNull(value, ErrorCode.INVALID_SHARDING_KEY);

        TableName table = entityClass.getDeclaredAnnotation(TableName.class);
        if (table == null || StringUtils.isBlank(table.value())) {
            return;
        }

        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            Sharding sharding = field.getDeclaredAnnotation(Sharding.class);
            if (sharding == null) {
                continue;
            }
            String tableName = table.value();
            Object shardingVal = "";

            // 策略
            Sharding.Strategy strategy = sharding.strategy();
            switch (strategy) {
                case MOD -> {
                    // 取模策略
                    int mod = sharding.mod();
                    if (mod <= 0) {
                        continue;
                    }
                    long shardingKey = Long.parseLong(String.valueOf(value));
                    shardingVal = shardingKey % mod;
                }
                default -> {
                    // ignore
                }
            }

            ShardingThreadLocal.set(tableName, shardingVal);

            break;
        }
    }

    private void clean() {
        ShardingThreadLocal.clean();
    }
}
