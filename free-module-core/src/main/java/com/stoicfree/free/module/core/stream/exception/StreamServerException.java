package com.stoicfree.free.module.core.stream.exception;

/**
 * @author zengzhifei
 * @date 2023/3/29 15:27
 */
public class StreamServerException extends RuntimeException {
    public StreamServerException() {
    }

    public StreamServerException(String msg) {
        super(msg);
    }
}