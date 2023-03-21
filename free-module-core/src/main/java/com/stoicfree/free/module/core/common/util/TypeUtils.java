package com.stoicfree.free.module.core.common.util;

import java.util.Collection;

import org.apache.commons.lang.ClassUtils;

/**
 * @author zengzhifei
 * @date 2023/3/21 12:42
 */
public class TypeUtils {
    public static boolean isCharacter(Class<?> typeClass) {
        return ClassUtils.isAssignable(typeClass, Character.class, true);
    }

    public static boolean isString(Class<?> typeClass) {
        return ClassUtils.isAssignable(typeClass, String.class, true);
    }

    public static boolean isBoolean(Class<?> typeClass) {
        return ClassUtils.isAssignable(typeClass, Boolean.class, true);
    }

    public static boolean isNumber(Class<?> typeClass) {
        return ClassUtils.isAssignable(typeClass, Number.class, true);
    }

    public static boolean isCollection(Class<?> typeClass) {
        return ClassUtils.isAssignable(typeClass, Collection.class, true);
    }

    public static boolean isAssignable(Class<?> typeClass, Class<?> toClass) {
        return ClassUtils.isAssignable(typeClass, toClass, true);
    }

    public static boolean isCustomObject(Class<?> typeClass) {
        return typeClass.getClassLoader() != null;
    }
}
