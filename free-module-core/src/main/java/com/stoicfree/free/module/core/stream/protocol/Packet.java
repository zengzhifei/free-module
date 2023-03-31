package com.stoicfree.free.module.core.stream.protocol;

import java.nio.ByteBuffer;

import com.stoicfree.free.module.core.common.gson.GsonUtil;
import com.stoicfree.free.module.core.stream.enums.Command;

import cn.hutool.core.io.BufferUtil;
import cn.hutool.socket.aio.AioSession;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zengzhifei
 * @date 2023/3/30 19:09
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Packet<T> {
    private Command command;
    private T payload;
}
