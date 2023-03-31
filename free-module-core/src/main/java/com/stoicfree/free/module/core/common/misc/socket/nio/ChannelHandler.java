package com.stoicfree.free.module.core.common.misc.socket.nio;

import java.nio.channels.SocketChannel;

/**
 * @author zengzhifei
 * @date 2023/3/31 16:28
 */
@FunctionalInterface
public interface ChannelHandler {
    void handle(SocketChannel socketChannel) throws Exception;
}