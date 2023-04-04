package com.stoicfree.free.module.core.common.misc.socket.nio.protocol;

import java.io.Serializable;
import java.util.UUID;

import com.stoicfree.free.module.core.common.gson.GsonUtil;

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
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class Packet<Command extends Enum<Command>> implements Serializable {
    /**
     * 请求唯一id
     */
    @Builder.Default
    private final String id = UUID.randomUUID().toString();

    /**
     * 协议版本
     */
    @Builder.Default
    private final long version = 1;

    /**
     * Command Class
     */
    @Builder.ObtainVia(method = "getCommandClass")
    private Class<Command> cc;

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
     * @param rawClazz
     * @param genericClasses
     *
     * @return
     */
    public <R extends T, T> R getPayload(Class<T> rawClazz, Class<?>... genericClasses) {
        return GsonUtil.fromJson(GsonUtil.toJson(payload), rawClazz, genericClasses);
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

    @SuppressWarnings("unchecked")
    private Class<Command> getCommandClass() {
        return (Class<Command>) command.getClass();
    }
}
