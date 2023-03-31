package com.stoicfree.free.module.core.common.misc.socket.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

import cn.hutool.core.io.BufferUtil;

/**
 * @author zengzhifei
 * @date 2023/3/31 17:50
 */
public class ChannelHelper {
    public static ByteBuffer readByteBuffer(SocketChannel channel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int read = channel.read(buffer);
        if (read > 0) {
            buffer.flip();
            return buffer;
        } else {
            return null;
        }
    }

    public static String readString(SocketChannel channel) throws IOException {
        ByteBuffer buffer = readByteBuffer(channel);
        if (buffer != null) {
            return BufferUtil.readUtf8Str(buffer);
        } else {
            return null;
        }
    }

    public static void write(SocketChannel channel, String... srcs) throws IOException {
        if (srcs != null && srcs.length > 0) {
            ByteBuffer[] byteBuffers = Arrays.stream(srcs).map(BufferUtil::createUtf8).toArray(ByteBuffer[]::new);
            channel.write(byteBuffers);
        }
    }
}
