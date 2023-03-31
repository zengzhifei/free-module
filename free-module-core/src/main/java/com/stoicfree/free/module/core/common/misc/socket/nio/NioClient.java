package com.stoicfree.free.module.core.common.misc.socket.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zengzhifei
 * @date 2023/3/31 16:13
 */
@Slf4j
public class NioClient extends Nio {
    private SocketChannel socketChannel;

    public NioClient(String host, int port) {
        init(new InetSocketAddress(host, port), false);
    }

    public NioClient(String host, int port, boolean block) {
        init(new InetSocketAddress(host, port), block);
    }

    public ByteBuffer blockingWrite(ByteBuffer... src) {
        if (!this.socketChannel.isBlocking()) {
            throw new UnsupportedOperationException("blocking write must running of blocking mode");
        }

        try {
            this.socketChannel.write(src);
            return ChannelHelper.readByteBuffer(this.socketChannel);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void nonblockingWrite(ByteBuffer... src) {
        if (this.socketChannel.isBlocking()) {
            throw new UnsupportedOperationException("nonblocking write must running of nonblocking mode");
        }

        try {
            this.socketChannel.write(src);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public SocketChannel getChannel() {
        return this.socketChannel;
    }

    private void init(InetSocketAddress address, boolean block) {
        try {
            // 创建一个SocketChannel对象
            this.socketChannel = SocketChannel.open();
            // 配置阻塞模式
            this.socketChannel.configureBlocking(block);
            // 连接server
            this.socketChannel.connect(address);

            if (block) {
                return;
            }

            // 创建一个选择器
            this.selector = Selector.open();
            // 把SocketChannel交给selector对象
            this.socketChannel.register(this.selector, SelectionKey.OP_READ);

            // 等待建立连接
            //noinspection StatementWithEmptyBody
            while (!this.socketChannel.finishConnect()) {
            }
        } catch (IOException e) {
            this.close(e);
        }
    }

    @Override
    public void close() {
        this.close(this.socketChannel);
    }
}
