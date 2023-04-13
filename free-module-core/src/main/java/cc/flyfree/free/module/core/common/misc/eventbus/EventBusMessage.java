package cc.flyfree.free.module.core.common.misc.eventbus;

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
