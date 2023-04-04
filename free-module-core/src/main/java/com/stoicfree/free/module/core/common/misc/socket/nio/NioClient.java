package com.stoicfree.free.module.core.common.misc.socket.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import com.stoicfree.free.module.core.common.misc.socket.nio.protocol.Protocol;

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

    public ByteBuffer blockingWrite(Protocol.Proto proto) {
        if (!this.socketChannel.isBlocking()) {
            throw new UnsupportedOperationException("blocking write must running of blocking mode");
        }

        ChannelIo.write(this.socketChannel, proto);

        return ChannelIo.read(this.socketChannel);
    }

    public void nonblockingWrite(Protocol.Proto proto) {
        if (this.socketChannel.isBlocking()) {
            throw new UnsupportedOperationException("nonblocking write must running of nonblocking mode");
        }

        ChannelIo.write(this.socketChannel, proto);
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
