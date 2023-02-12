package com.stoicfree.free.db.module.support;

import java.lang.reflect.Field;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

import com.baomidou.mybatisplus.annotation.TableName;
import com.stoicfree.free.db.module.annotation.Sharding;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zengzhifei
 * @date 2022/8/11 11:40
 */
@Slf4j
@SuppressWarnings("unchecked")
public class BaseSharding<E> {
    public E sharding() {
        TableName table = this.getClass().getDeclaredAnnotation(TableName.class);
        if (table == null || StringUtils.isBlank(table.value())) {
            return (E) this;
        }

        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            Sharding sharding = field.getDeclaredAnnotation(Sharding.class);
            if (sharding == null) {
                continue;
            }
            String tableName = table.value();
            int mod = sharding.mod();
            Object value = null;
            try {
                field.setAccessible(true);
                value = field.get(this);
            } catch (Exception e) {
                log.error("sharding table error", e);
            }

            Assert.notNull(value, "sharding key's value is null");
            long shardingKey = Long.parseLong(String.valueOf(value));
            long shardingVal = shardingKey % mod;
            ShardingThreadLocal.set(tableName, shardingVal);

            return (E) this;
        }

        return (E) this;
    }
}
