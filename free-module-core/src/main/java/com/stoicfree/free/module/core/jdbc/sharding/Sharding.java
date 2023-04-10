package com.stoicfree.free.module.core.jdbc.sharding;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.stoicfree.free.module.core.common.util.LambdaUtils;
import com.stoicfree.free.module.core.jdbc.annotation.TableSharding;

/**
 * @author zengzhifei
 * @date 2023/4/7 17:34
 */
public class Sharding<T, R> {
    private final SharingStrategy<T, R> strategy;

    private Sharding(SharingStrategy<T, R> strategy) {
        this.strategy = strategy;
    }

    @SuppressWarnings("unchecked")
    public static <T, R> Sharding<T, R> key(SFunction<T, R> function) {
        Class<T> entityClass = LambdaUtils.getRealClass(function);
        TableSharding tableSharding = entityClass.getDeclaredAnnotation(TableSharding.class);
        SharingStrategy<T, ?> strategy;
        switch (tableSharding.strategy()) {
            case MOD:
            default:
                strategy = new Mod<>(entityClass, tableSharding);
                break;
        }
        return new Sharding<>((SharingStrategy<T, R>) strategy);
    }

    public Sharding<T, R> shard(R value) {
        String tableName = strategy.getTableName();
        String suffix = strategy.sharding(value);
        ShardingThreadLocal.set(tableName, suffix);
        return this;
    }

    public void remove() {
        ShardingThreadLocal.remove();
    }

    private static class Mod<T> extends SharingStrategy<T, Long> {
        private Mod(Class<T> entityClass, TableSharding tableSharding) {
            super(entityClass, tableSharding);
        }

        @Override
        public String sharding(Long value) {
            return String.valueOf(value % getTableSharding().size());
        }
    }
}
