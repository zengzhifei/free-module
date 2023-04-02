package com.stoicfree.free.module.core.stream.protocol;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zengzhifei
 * @date 2023/3/29 15:27
 */
@Getter
@AllArgsConstructor
public enum Command {
    // 协议命令
    PROVIDER_AUTH, PUBLISH, DELAY_PUBLISH, CONSUMER_AUTH, CONSUME;
}
