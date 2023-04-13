package cc.flyfree.free.module.core.common.support;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * @author zengzhifei
 * @date 2023/3/16 20:31
 */
public class AnnotatedBeanContainer implements ApplicationListener<ContextRefreshedEvent> {
    private final Class<? extends Annotation> beanAnnotation;
    private final Class<? extends Annotation> methodAnnotation;
    private final Map<Object, List<Method>> container = new HashMap<>();

    public AnnotatedBeanContainer(Class<? extends Annotation> beanAnnotation,
                                  Class<? extends Annotation> methodAnnotation) {
        this.beanAnnotation = beanAnnotation;
        this.methodAnnotation = methodAnnotation;
    }

    public Set<Object> getAnnotatedBeans() {
        return container.keySet();
    }

    public Map<Object, List<Method>> getAnnotatedBeanMethods() {
        return container;
    }

    protected void afterInitAnnotatedBeanContainer() {
        return;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        ApplicationContext applicationContext = contextRefreshedEvent.getApplicationContext();
        if (applicationContext.getParent() != null) {
            return;
        }

        Map<String, Object> beanMap = new HashMap<>(8);
        if (beanAnnotation != null) {
            beanMap = applicationContext.getBeansWithAnnotation(beanAnnotation);
        } else {
            String[] beanDefinitionNames = applicationContext.getBeanNamesForType(Object.class, false, true);
            for (String beanDefinitionName : beanDefinitionNames) {
                Object bean = applicationContext.getBean(beanDefinitionName);
                beanMap.put(beanDefinitionName, bean);
            }
        }

        if (methodAnnotation != null) {
            for (Map.Entry<String, Object> entry : beanMap.entrySet()) {
                Object bean = entry.getValue();
                Method[] methods = bean.getClass().getDeclaredMethods();
                if (methods.length == 0) {
                    continue;
                }

                List<Method> annotationMethods = new ArrayList<>();
                for (Method method : methods) {
                    Annotation methodDeclaredAnnotation = method.getDeclaredAnnotation(methodAnnotation);
                    if (methodDeclaredAnnotation == null) {
                        continue;
                    }
                    method.setAccessible(true);
                    annotationMethods.add(method);
                }
                if (!annotationMethods.isEmpty()) {
                    container.put(bean, annotationMethods);
                }
            }
        } else {
            for (Map.Entry<String, Object> entry : beanMap.entrySet()) {
                Object bean = entry.getValue();
                container.put(bean, new ArrayList<>());
            }
        }

        afterInitAnnotatedBeanContainer();
    }
}
