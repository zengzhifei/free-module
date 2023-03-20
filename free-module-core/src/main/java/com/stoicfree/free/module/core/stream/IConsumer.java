package com.stoicfree.free.module.core.stream;

import java.util.List;

import com.stoicfree.free.module.core.stream.domain.Message;

/**
 * @author zengzhifei
 * @date 2023/3/19 23:59
 */
public interface IConsumer {
    /**
     * 单条消费消息
     *
     * @param message
     */
    void consume(Message message);

    /**
     * 批量消费消息
     *
     * @param messages
     */
    default void batchConsume(List<Message> messages) {
        for (Message message : messages) {
            consume(message);
        }
    }
}
