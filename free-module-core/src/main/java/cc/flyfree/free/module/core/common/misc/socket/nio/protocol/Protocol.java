package cc.flyfree.free.module.core.common.misc.socket.nio.protocol;

import java.nio.ByteBuffer;
import java.util.Map;

import cc.flyfree.free.module.core.common.exception.IoRuntimeException;
import cc.flyfree.free.module.core.common.gson.GsonUtil;
import cc.flyfree.free.module.core.common.support.Func;
import cc.flyfree.free.module.core.common.util.LambdaUtils;

import cn.hutool.core.io.BufferUtil;
import cn.hutool.core.util.ClassLoaderUtil;

/**
 * @author zengzhifei
 * @date 2023/3/30 19:34
 */
public final class Protocol {
    public static <Command extends Enum<Command>> Proto encode(Command command, Object payload) {
        Packet<Command> packet = Packet.<Command>builder().command(command).payload(payload).build()
                .toBuilder().build();
        return encode(packet);
    }

    public static <Packet> Proto encode(Packet packet) {
        ByteBuffer bodyBuffer = BufferUtil.createUtf8(GsonUtil.toJson(packet));
        return new Proto(bodyBuffer);
    }

    public static <Command extends Enum<Command>> Packet<Command> decode(ByteBuffer buffer) {
        String body = BufferUtil.readUtf8Str(buffer);
        Map<String, Object> map = GsonUtil.fromJsonToMap(body, String.class, Object.class);
        Func<Packet<?>, Class<?>> ccFunc = Packet::getCc;
        String ccKey = LambdaUtils.getFieldName(ccFunc);
        Class<?> cc = ClassLoaderUtil.loadClass((String) map.get(ccKey));
        return GsonUtil.fromJson(body, Packet.class, cc);
    }

    public static class Proto {
        public static final int HEADER_LENGTH = 5;
        private final ByteBuffer bodyBuffer;

        private Proto(ByteBuffer bodyBuffer) {
            this.bodyBuffer = bodyBuffer;
        }

        public ByteBuffer pack() {
            int bodyLength = bodyBuffer.remaining();
            int maxLength = (int) (Math.pow(10, HEADER_LENGTH) - 1);
            if (bodyLength > maxLength) {
                throw new IoRuntimeException(String.format("packet size[%d] more than max length[%d]", bodyLength,
                        maxLength));
            }
            String header = String.format("%0" + HEADER_LENGTH + "d", bodyLength);
            ByteBuffer headerBuffer = BufferUtil.createUtf8(header);
            int headerLen = headerBuffer.remaining();

            ByteBuffer fullBuffer = ByteBuffer.allocate(headerLen + bodyLength);
            fullBuffer.put(headerBuffer.array()).put(bodyBuffer.array()).flip();

            return fullBuffer;
        }
    }
}
