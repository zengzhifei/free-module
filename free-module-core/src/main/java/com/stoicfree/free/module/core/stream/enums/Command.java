package com.stoicfree.free.module.core.stream.enums;

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
    PROVIDER_AUTH, PUBLISH
}
