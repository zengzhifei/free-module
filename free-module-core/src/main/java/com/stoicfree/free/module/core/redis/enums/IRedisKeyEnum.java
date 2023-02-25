package com.stoicfree.free.module.core.redis.enums;

import java.util.StringJoiner;

/**
 * @author zengzhifei
 * @date 2023/2/14 14:45
 */
public interface IRedisKeyEnum {
    String getKey();

    Integer getExpires();

    default String build(Object... params) {
        if (params == null || params.length == 0) {
            return this.getKey();
        }
        StringJoiner stringJoiner = new StringJoiner("_");
        stringJoiner.add(this.getKey());
        for (Object param : params) {
            stringJoiner.add((String) param);
        }
        return stringJoiner.toString();
    }
}