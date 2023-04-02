package com.stoicfree.free.module.core.common.misc.socket.nio.protocol;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author zengzhifei
 * @date 2023/3/30 19:09
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Packet<Command extends Enum<Command>> {
    /**
     * 请求唯一id
     */
    @Builder.Default
    private final String id = UUID.randomUUID().toString();

    /**
     * 协议版本
     */
    @Builder.Default
    private final double version = 0;

    /**
     * 消息命令
     */
    private Command command;

    /**
     * 消息载体
     */
    private Object payload;

    /**
     * 获取指定格式消息载体
     *
     * @param clazz
     * @param <T>
     *
     * @return
     */
    public <T> T getPayload(Class<T> clazz) {
        return clazz.cast(payload);
    }

    /**
     * 设置新消息载体
     *
     * @param payload
     *
     * @return
     */
    public Packet<Command> newPayload(Object payload) {
        setPayload(payload);
        return this;
    }
}
