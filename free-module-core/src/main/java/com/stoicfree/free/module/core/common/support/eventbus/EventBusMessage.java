package com.stoicfree.free.module.core.common.support.eventbus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zengzhifei
 * @date 2023/2/2 12:20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventBusMessage<T> {
    private Enum<?> event;
    private T data;
}
