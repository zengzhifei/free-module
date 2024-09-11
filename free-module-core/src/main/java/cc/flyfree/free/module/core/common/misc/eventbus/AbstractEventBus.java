package cc.flyfree.free.module.core.common.misc.eventbus;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.Subscribe;

import cc.flyfree.free.module.core.common.support.ExecutorHelper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * 异步事件
 *
 * @author zengzhifei
 * @date 2023/2/2 11:13
 */
@Slf4j
public abstract class AbstractEventBus implements EventBus {
    private static final AsyncEventBus EVENT_BUS = new AsyncEventBus(
            ExecutorHelper.newCacheThreadPool("async-event-bus", 1, 2, 1024)
    );

    @PostConstruct
    public void init() {
        EVENT_BUS.register(this);
    }

    @Override
    public final <T> void post(EventBusMessage<T> message) {
        if (message == null || message.getEvent() == null) {
            return;
        }
        EVENT_BUS.post(message);
    }

    @Subscribe
    @AllowConcurrentEvents
    public abstract <T> void subscribe(EventBusMessage<T> message);
}
