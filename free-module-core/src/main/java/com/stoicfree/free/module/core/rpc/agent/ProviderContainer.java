package com.stoicfree.free.module.core.rpc.agent;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.AnnotationConfigurationException;

import com.stoicfree.free.module.core.rpc.annotation.ProviderClass;
import com.stoicfree.free.module.core.rpc.annotation.ProviderMethod;

import javafx.util.Pair;

/**
 * @author zengzhifei
 * @date 2023/2/25 23:20
 */
public class ProviderContainer implements ApplicationListener<ContextRefreshedEvent> {
    private final Map<String, Pair<Object, Method>> providers = new HashMap<>();

    public Pair<Object, Method> getProvider(String id) {
        return providers.get(id);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if (contextRefreshedEvent.getApplicationContext().getParent() == null) {
            ApplicationContext applicationContext = contextRefreshedEvent.getApplicationContext();
            Map<String, Object> map = applicationContext.getBeansWithAnnotation(ProviderClass.class);
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String name = entry.getKey();
                Object bean = entry.getValue();
                Method[] methods = bean.getClass().getDeclaredMethods();
                if (methods.length == 0) {
                    continue;
                }

                for (Method method : methods) {
                    ProviderMethod providerMethod = method.getDeclaredAnnotation(ProviderMethod.class);
                    if (providerMethod == null) {
                        continue;
                    }
                    String id = providerMethod.id();
                    if (StringUtils.isBlank(id)) {
                        throw new AnnotationConfigurationException(String.format("%s %s ProviderMethod id not be blank",
                                name, method.getName()));
                    }
                    if (providers.containsKey(id)) {
                        throw new AnnotationConfigurationException(
                                String.format("%s %s ProviderMethod id not be repeat", name, method.getName()));
                    }

                    providers.put(id, new Pair<>(bean, method));
                }
            }
        }
    }
}
