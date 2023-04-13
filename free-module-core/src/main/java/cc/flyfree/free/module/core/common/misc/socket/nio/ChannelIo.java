package cc.flyfree.free.module.core.common.misc.socket.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import cc.flyfree.free.module.core.common.exception.IoRuntimeException;
import cc.flyfree.free.module.core.common.misc.socket.nio.protocol.Packet;
import cc.flyfree.free.module.core.common.misc.socket.nio.protocol.Protocol;

import cn.hutool.core.io.BufferUtil;

/**
 * @author zengzhifei
 * @date 2023/3/31 17:50
 */
public class ChannelIo {
    /**
     * 从channel按标准协议读取流
     *
     * @param channel
     *
     * @return
     */
    public static ByteBuffer read(SocketChannel channel) {
        try {
            ByteBuffer headerBuffer = ByteBuffer.allocate(Protocol.Proto.HEADER_LENGTH);
            do {
                channel.read(headerBuffer);
            } while (headerBuffer.hasRemaining());
            headerBuffer.flip();

            String header = BufferUtil.readUtf8Str(headerBuffer);
            int bodyLength = Integer.parseInt(header);
            ByteBuffer bodyBuffer = ByteBuffer.allocate(bodyLength);
            do {
                channel.read(bodyBuffer);
            } while (bodyBuffer.hasRemaining());
            bodyBuffer.flip();

            return bodyBuffer;
        } catch (IOException e) {
            throw new IoRuntimeException(e);
        }
    }

    /**
     * 从channel按标准协议读取流，并转为默认协议载体
     *
     * @param channel
     *
     * @return
     */
    public static <Command extends Enum<Command>> Packet<Command> readout(SocketChannel channel) {
        try {
            ByteBuffer buffer = read(channel);
            return Protocol.decode(buffer);
        } catch (Exception e) {
            throw new IoRuntimeException(e);
        }
    }

    /**
     * 向channel按标准协议写入流
     *
     * @param channel
     * @param proto
     */
    public static void write(SocketChannel channel, Protocol.Proto proto) {
        try {
            ByteBuffer buffer = proto.pack();
            channel.write(buffer);
        } catch (IOException e) {
            throw new IoRuntimeException(e);
        }
    }

    /**
     * 向channel按标准协议写入默认协议载体流
     *
     * @param channel
     * @param packet
     */
    public static <Command extends Enum<Command>> void writeIn(SocketChannel channel, Packet<Command> packet) {
        try {
            Protocol.Proto proto = Protocol.encode(packet);
            write(channel, proto);
        } catch (Exception e) {
            throw new IoRuntimeException(e);
        }
    }
}
