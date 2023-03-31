package com.stoicfree.free.module.core.stream.protocol;

import java.nio.ByteBuffer;

import com.google.gson.reflect.TypeToken;
import com.stoicfree.free.module.core.common.gson.GsonUtil;
import com.stoicfree.free.module.core.stream.enums.Command;

import cn.hutool.core.io.BufferUtil;

/**
 * @author zengzhifei
 * @date 2023/3/30 19:34
 */
public final class Protocol {
    public static <T> ByteBuffer encode(Command command, T payload) {
        Packet<Object> packet = Packet.builder().command(Command.PROVIDER_AUTH).payload(payload).build();
        return encode(packet);
    }

    public static <T> ByteBuffer encode(Packet<T> packet) {
        return BufferUtil.createUtf8(GsonUtil.toJson(packet));
    }

    public static <T> Packet<T> decode(ByteBuffer buffer) {
        String data = BufferUtil.readUtf8Str(buffer);
        return GsonUtil.fromJson(data, new TypeToken<Packet<T>>() {
        }.getType());
    }
}
