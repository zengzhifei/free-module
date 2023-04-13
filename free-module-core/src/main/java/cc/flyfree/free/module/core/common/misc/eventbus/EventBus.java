package cc.flyfree.free.module.core.common.misc.eventbus;

/**
 * @author zengzhifei
 * @date 2023/2/12 20:56
 */
public interface EventBus {
    <T> void post(EventBusMessage<T> message);
}
