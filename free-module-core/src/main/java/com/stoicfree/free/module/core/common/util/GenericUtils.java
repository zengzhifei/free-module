package com.stoicfree.free.module.core.common.util;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import com.google.common.reflect.TypeToken;

import cn.hutool.core.util.TypeUtil;

/**
 * @author zengzhifei
 * @date 2023/3/17 15:23
 */
public class GenericUtils {
    public static <T> Class<?> getGenericClass(TypeToken<T> typeToken, int index) {
        Class<?> clazz = TypeUtil.getClass(typeToken.getRawType());
        TypeVariable<? extends Class<?>>[] clazzTypeParameters = clazz.getTypeParameters();
        if (clazzTypeParameters.length == 0) {
            return null;
        }
        TypeToken<?> genericTypeToken = typeToken.resolveType(clazzTypeParameters[index]);
        Type genericType = genericTypeToken.getType();
        return TypeUtil.getClass(genericType);
    }
}
