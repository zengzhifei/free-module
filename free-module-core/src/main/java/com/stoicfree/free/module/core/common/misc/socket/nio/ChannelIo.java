package com.stoicfree.free.module.core.common.misc.socket.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.stoicfree.free.module.core.common.misc.socket.nio.protocol.Packet;
import com.stoicfree.free.module.core.common.misc.socket.nio.protocol.Protocol;

/**
 * @author zengzhifei
 * @date 2023/3/31 17:50
 */
public class ChannelIo {
    public static ByteBuffer read(SocketChannel channel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int read = channel.read(buffer);
        if (read > 0) {
            buffer.flip();
            return buffer;
        } else {
            return null;
        }
    }

    public static <Command extends Enum<Command>> Packet<Command> readout(SocketChannel channel) throws IOException {
        ByteBuffer buffer = read(channel);
        return Protocol.decode(buffer);
    }

    public static void write(SocketChannel channel, ByteBuffer... buffers) throws IOException {
        channel.write(buffers);
    }

    public static <Command extends Enum<Command>> void writeIn(SocketChannel channel, Packet<Command> packet)
            throws IOException {
        ByteBuffer buffer = Protocol.encode(packet);
        write(channel, buffer);
    }
}
