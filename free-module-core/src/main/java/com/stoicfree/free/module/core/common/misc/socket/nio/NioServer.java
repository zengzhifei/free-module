package com.stoicfree.free.module.core.common.misc.socket.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zengzhifei
 * @date 2023/3/31 15:38
 */
@Slf4j
public class NioServer extends Nio {
    private ServerSocketChannel serverSocketChannel;

    public NioServer(int port) {
        this.init(new InetSocketAddress(port));
    }

    private void init(InetSocketAddress address) {
        try {
            // 打开服务器套接字通道
            this.serverSocketChannel = ServerSocketChannel.open();
            // 设置为非阻塞状态
            this.serverSocketChannel.configureBlocking(false);
            // 绑定端口号
            this.serverSocketChannel.bind(address);
            // 打开一个选择器
            this.selector = Selector.open();
            // 服务器套接字注册到Selector中 并指定Selector监控连接事件
            this.serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            this.close(e);
        }
    }

    @Override
    protected void handleAccept(SelectionKey selectionKey) {
        try {
            // 获取server channel
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
            // 获取client连接
            SocketChannel socketChannel = serverSocketChannel.accept();
            // 设置为非阻塞
            socketChannel.configureBlocking(false);
            // 将新连接的通道的可读事件注册到选择器上
            // 新建立的socketChannel客户端传输通道，也要注册到同一个选择器上，
            // 这样就能使用同一个选择线程不断地对所有的注册通道进行选择键的查询。
            socketChannel.register(this.selector, SelectionKey.OP_READ);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        this.close(this.serverSocketChannel);
    }
}
