package com.stoicfree.free.common.module.domain;

import java.util.UUID;

import com.stoicfree.free.common.module.enums.ErrorCode;

import lombok.Data;

/**
 * @author zengzhifei
 * @date 2022/8/11 16:09
 */
@Data
public class Result<T> {
    private int errno;
    private String msg;
    private T data;
    private String requestId;

    public Result() {
        this.requestId = UUID.randomUUID().toString();
    }

    public Result(int errno, String msg, T data) {
        this.errno = errno;
        this.msg = msg;
        this.data = data;
        this.requestId = UUID.randomUUID().toString();
    }

    public static <T> Result<T> ok() {
        return new Result<>(ErrorCode.OK.getCode(), ErrorCode.OK.getMsg(), null);
    }

    public static <T> Result<T> ok(T data) {
        return new Result<>(ErrorCode.OK.getCode(), ErrorCode.OK.getMsg(), data);
    }

    public static <T> Result<T> ok(T data, String msg) {
        return new Result<>(ErrorCode.OK.getCode(), msg, data);
    }

    public static <T> Result<T> fail(String msg) {
        return new Result<>(ErrorCode.UNKNOWN_ERROR.getCode(), msg, null);
    }

    public static <T> Result<T> fail(int errno, String msg) {
        return new Result<>(errno, msg, null);
    }

    public boolean success() {
        return this.errno == ErrorCode.OK.getCode();
    }
}
