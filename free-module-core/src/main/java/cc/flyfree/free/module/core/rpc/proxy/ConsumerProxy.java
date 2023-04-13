package cc.flyfree.free.module.core.rpc.proxy;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang.StringUtils;

import cc.flyfree.free.module.core.rpc.config.ConsumerProperties;
import cc.flyfree.free.module.core.rpc.exception.ConsumerException;

/**
 * @author zengzhifei
 * @date 2023/2/26 00:48
 */
public class ConsumerProxy {
    private final AtomicLong counter = new AtomicLong(0);
    private final Map<Class<?>, Object> clazzCache = new HashMap<>();

    private final ConsumerProperties consumerProperties;
    private final String[] hosts;

    public ConsumerProxy(ConsumerProperties consumerProperties, String... hosts) {
        if (hosts.length == 0) {
            throw new ConsumerException("Consumer hosts not be empty");
        }
        for (int i = 0; i < hosts.length; i++) {
            if (hosts[i].startsWith("/")) {
                throw new ConsumerException("Consumer hosts not start /");
            }

            if (!StringUtils.startsWithIgnoreCase("http://", hosts[i])
                    || !StringUtils.startsWithIgnoreCase("https://", hosts[i])) {
                hosts[i] = String.format("http://%s", hosts[i]);
            }
        }

        this.consumerProperties = consumerProperties;
        this.hosts = hosts;
    }

    public <T> T proxy(Class<T> clazz) {
        counter.incrementAndGet();
        if (clazzCache.containsKey(clazz)) {
            return clazz.cast(clazzCache.get(clazz));
        }

        ConsumerInvocationHandler invocationHandler = new ConsumerInvocationHandler(this);
        Object instance = Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] {clazz}, invocationHandler);
        T object = clazz.cast(instance);

        clazzCache.put(clazz, object);

        return object;
    }

    public String getHost() {
        int index = (int) counter.get() % hosts.length;
        return hosts[index];
    }

    public long getCount() {
        return counter.get();
    }

    public ConsumerProperties getConsumerProperties() {
        return consumerProperties;
    }
}
