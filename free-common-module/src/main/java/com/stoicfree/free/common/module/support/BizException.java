package com.stoicfree.free.common.module.support;

import com.stoicfree.free.common.module.enums.ErrorCode;

import lombok.Getter;

/**
 * @author zengzhifei
 * @date 2022/8/11 17:05
 */
public class BizException extends RuntimeException {
    @Getter
    private final int code;

    public BizException(ErrorCode errorCode) {
        super(errorCode.getMsg());
        this.code = errorCode.getCode();
    }

    public BizException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }

    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }
}
