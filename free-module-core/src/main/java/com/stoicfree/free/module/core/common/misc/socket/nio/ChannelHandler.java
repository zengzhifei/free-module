package com.stoicfree.free.module.core.common.misc.socket.nio;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * @author zengzhifei
 * @date 2023/3/31 16:28
 */
@FunctionalInterface
public interface ChannelHandler {
    /**
     * IO事件处理器
     *
     * @param selectionKey
     * @param channel
     *
     * @throws Exception
     */
    void handle(SelectionKey selectionKey, SocketChannel channel) throws Exception;
}