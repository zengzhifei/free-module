package com.stoicfree.free.module.core.common.exception;

/**
 * @author zengzhifei
 * @date 2023/3/29 15:27
 */
public class IoRuntimeException extends RuntimeException {
    public IoRuntimeException() {
    }

    public IoRuntimeException(String message) {
        super(message);
    }

    public IoRuntimeException(Exception e) {
        super(e.getMessage());
    }
}