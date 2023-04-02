package com.stoicfree.free.module.core.common.misc.socket.nio.protocol;

import java.nio.ByteBuffer;
import java.util.Map;

import com.stoicfree.free.module.core.common.gson.GsonUtil;
import com.stoicfree.free.module.core.common.support.Func;
import com.stoicfree.free.module.core.common.util.LambdaUtils;

import cn.hutool.core.io.BufferUtil;
import cn.hutool.core.util.ClassLoaderUtil;

/**
 * @author zengzhifei
 * @date 2023/3/30 19:34
 */
public class Protocol {
    public static <Command extends Enum<Command>, T> ByteBuffer encode(Command command, T payload) {
        Packet<Command> packet = Packet.<Command>builder().command(command).payload(payload).build()
                .toBuilder().build();
        return encode(packet);
    }

    public static <Command extends Enum<Command>, T> ByteBuffer encode(Packet<Command> packet) {
        return BufferUtil.createUtf8(GsonUtil.toJson(packet));
    }

    public static <Command extends Enum<Command>> Packet<Command> decode(ByteBuffer buffer) {
        String data = BufferUtil.readUtf8Str(buffer);
        Map<String, Object> map = GsonUtil.fromJsonToMap(data, String.class, Object.class);
        Func<Packet<?>, Class<?>> ccFunc = Packet::getCc;
        String ccKey = LambdaUtils.getFieldName(ccFunc);
        Class<?> cc = ClassLoaderUtil.loadClass((String) map.get(ccKey));
        return GsonUtil.fromJson(data, Packet.class, cc);
    }
}
