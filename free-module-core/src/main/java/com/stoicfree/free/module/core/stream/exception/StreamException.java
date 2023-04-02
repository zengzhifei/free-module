package com.stoicfree.free.module.core.stream.exception;

/**
 * @author zengzhifei
 * @date 2023/3/29 15:27
 */
public class StreamException extends RuntimeException {
    public StreamException() {
    }

    public StreamException(String msg) {
        super(msg);
    }
}