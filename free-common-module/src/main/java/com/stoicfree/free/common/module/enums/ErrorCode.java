package com.stoicfree.free.common.module.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zengzhifei
 * @date 2022/8/11 16:16
 */
@AllArgsConstructor
public enum ErrorCode {
    // 通用错误码
    OK(0, "success"),
    UNKNOWN_ERROR(500, "对不起，服务器出错了，请稍候再试"),
    EMPTY_PARAMS(10001, "参数为空"),
    INVALID_PARAMS(10002, "参数无效"),
    REQUEST_EXPIRED(10003, "请求过期"),
    ILLEGAL_REQUEST(10004, "非法请求"),
    FORBID_OPERATE(10005, "禁止操作"),
    ABNORMAL_STATUS(10006, "状态异常"),
    IO_EXCEPTION(10007, "服务器异常"),
    INVALID_SHARDING_KEY(10008, "分表键无效"),
    FREQUENT_OPERATIONS(10009, "操作频繁，请稍候重试"),
    REQUEST_FAIL(10010, "请求失败"),
    ;

    @Getter
    private int code;
    @Getter
    private String msg;
}
