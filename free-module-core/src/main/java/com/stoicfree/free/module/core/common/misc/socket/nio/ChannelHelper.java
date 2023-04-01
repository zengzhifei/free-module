package com.stoicfree.free.module.core.common.misc.socket.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author zengzhifei
 * @date 2023/3/31 17:50
 */
public class ChannelHelper {
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

    public static void write(SocketChannel channel, ByteBuffer... buffers) throws IOException {
        channel.write(buffers);
    }
}
