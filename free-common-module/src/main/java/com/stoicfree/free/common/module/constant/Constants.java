package com.stoicfree.free.common.module.constant;

/**
 * @author zengzhifei
 * @date 2022/8/23 20:33
 */
public class Constants {
    /**
     * 时间
     */
    public static final Integer SECOND = 1;
    public static final Integer MINUTE = 60 * SECOND;
    public static final Integer HOUR = 60 * MINUTE;
    public static final Integer DAY = 24 * HOUR;
    public static final Integer WEEK = 7 * DAY;
    public static final Integer MONTH = 30 * DAY;
    public static final Integer YEAR = 12 * MONTH;

    /**
     * 金额
     */
    public static final Long FEN = 1L;
    public static final Long JIAO = 10 * FEN;
    public static final Long YUAN = 10 * JIAO;
    public static final Long WAN = 10000 * YUAN;

    /**
     * 中文汉字字符集
     */
    public static final String REGEX_CHINESE = "\u2E80-\u2EFF\u2F00-\u2FDF\u31C0-\u31EF\u3400-\u4DBF\u4E00-\u9FFF"
            + "\uF900-\uFAFF\uD840\uDC00-\uD869\uDEDF\uD869\uDF00-\uD86D\uDF3F\uD86D\uDF40-\uD86E\uDC1F\uD86E\uDC20"
            + "-\uD873\uDEAF\uD87E\uDC00-\uD87E\uDE1F";

    /**
     * mybatis
     */
    public static final String LIMIT_1 = "limit 1";
}
