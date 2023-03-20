package com.stoicfree.free.module.core.stream.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zengzhifei
 * @date 2023/3/20 14:18
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private String messageId;
    private String message;
}
