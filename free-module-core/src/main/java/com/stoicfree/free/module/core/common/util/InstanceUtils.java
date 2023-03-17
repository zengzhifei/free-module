package com.stoicfree.free.module.core.common.util;

/**
 * @author zengzhifei
 * @date 2023/3/17 15:40
 */
public class InstanceUtils {
    @SuppressWarnings("unchecked")
    public static <T> T newInstance(Class<?> clazz) {
        try {
            return (T) clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
