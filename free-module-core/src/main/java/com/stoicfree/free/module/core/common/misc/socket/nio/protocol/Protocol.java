package com.stoicfree.free.module.core.common.misc.socket.nio.protocol;

import java.nio.ByteBuffer;

import com.google.gson.reflect.TypeToken;
import com.stoicfree.free.module.core.common.gson.GsonUtil;

import cn.hutool.core.io.BufferUtil;

/**
 * @author zengzhifei
 * @date 2023/3/30 19:34
 */
public class Protocol {
    public static <Command extends Enum<Command>, T> ByteBuffer encode(Command command, T payload) {
        Packet<Command> packet = Packet.<Command>builder().command(command).payload(payload).build();
        return BufferUtil.createUtf8(GsonUtil.toJson(packet));
    }

    public static <Command extends Enum<Command>> Packet<Command> decode(ByteBuffer buffer) {
        String data = BufferUtil.readUtf8Str(buffer);
        return GsonUtil.fromJson(data, new TypeToken<Packet<Command>>() {
        }.getType());
    }

    public static void main(String[] args) {
        System.out.println(GsonUtil.toJson(true));
    }
}
