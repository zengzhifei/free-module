package com.stoicfree.free.module.core.common.support;

import cn.hutool.core.lang.Snowflake;

/**
 * @author zengzhifei
 * @date 2023/2/17 21:33
 */
public class ID {
    public static final Snowflake SNOWFLAKE;
    public static final ShortSnowflake SHORT_SNOWFLAKE;

    static {
        SNOWFLAKE = new Snowflake();
        SHORT_SNOWFLAKE = new ShortSnowflake();
    }
}
