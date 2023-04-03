package com.stoicfree.free.module.core.common.misc.socket.nio;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.thread.GlobalThreadPool;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zengzhifei
 * @date 2023/3/31 16:28
 */
@Slf4j
public abstract class Nio implements Closeable {
    protected ChannelHandler handler;
    protected Selector selector;

    @SuppressWarnings("unchecked")
    public <T extends Nio> T registerChannelHandler(ChannelHandler handler) {
        this.handler = handler;
        return (T) this;
    }

    public void start() {
        GlobalThreadPool.execute(this::listen);
    }

    protected void listen() {
        try {
            while (this.selector != null && this.selector.isOpen() && 0 != this.selector.select()) {
                // 返回已选择键的集合
                Iterator<SelectionKey> iterator = this.selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    handle(iterator.next());
                    iterator.remove();
                }
            }
        } catch (IOException e) {
            close(e);
        }
    }

    protected void handleAccept(SelectionKey selectionKey) {
    }

    protected void handleRead(SelectionKey selectionKey) {
        if (this.handler == null) {
            log.warn("channel handler not register");
            return;
        }

        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        try {
            this.handler.handle(selectionKey, socketChannel);
        } catch (Exception e) {
            log.error("channel was closed, because handler handle error", e);
        }
    }

    protected void close(IOException e) {
        try {
            close();
            throw new RuntimeException(e);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    protected void close(Closeable... closeables) {
        IoUtil.close(this.selector);
        if (closeables != null && closeables.length > 0) {
            for (Closeable closeable : closeables) {
                IoUtil.close(closeable);
            }
        }
    }

    private void handle(SelectionKey selectionKey) {
        // 客户端请求就绪
        if (selectionKey.isAcceptable()) {
            handleAccept(selectionKey);
        }

        // 读事件就绪
        if (selectionKey.isReadable()) {
            handleRead(selectionKey);
        }
    }
}
