package com.chafree.free.common.module.enums;

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
    EMPTY_PARAMS(100, "参数为空"),
    INVALID_PARAMS(101, "参数无效"),
    REQUEST_EXPIRED(102, "请求过期"),
    ILLEGAL_REQUEST(103, "非法请求"),
    FORBID_OPERATE(104, "禁止操作"),
    ABNORMAL_STATUS(105, "状态异常");

    @Getter
    private int code;
    @Getter
    private String msg;
}
