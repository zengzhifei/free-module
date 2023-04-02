package com.stoicfree.free.module.core.common.misc.socket.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.stoicfree.free.module.core.common.exception.IoRuntimeException;
import com.stoicfree.free.module.core.common.misc.socket.nio.protocol.Packet;
import com.stoicfree.free.module.core.common.misc.socket.nio.protocol.Protocol;

/**
 * @author zengzhifei
 * @date 2023/3/31 17:50
 */
public class ChannelIo {
    public static ByteBuffer read(SocketChannel channel) {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int read = channel.read(buffer);
            if (read > 0) {
                buffer.flip();
                return buffer;
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new IoRuntimeException(e);
        }
    }

    public static <Command extends Enum<Command>> Packet<Command> readout(SocketChannel channel) {
        try {
            ByteBuffer buffer = read(channel);
            return Protocol.decode(buffer);
        } catch (Exception e) {
            throw new IoRuntimeException(e);
        }
    }

    public static void write(SocketChannel channel, ByteBuffer... buffers) {
        try {
            channel.write(buffers);
        } catch (IOException e) {
            throw new IoRuntimeException(e);
        }
    }

    public static <Command extends Enum<Command>> void writeIn(SocketChannel channel, Packet<Command> packet) {
        try {
            ByteBuffer buffer = Protocol.encode(packet);
            write(channel, buffer);
        } catch (Exception e) {
            throw new IoRuntimeException(e);
        }
    }
}
