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

    public <T> T getPayload(Class<T> clazz) {
        return clazz.cast(payload);
    }
}
