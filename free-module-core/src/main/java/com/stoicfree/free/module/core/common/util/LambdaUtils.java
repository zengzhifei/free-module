package com.stoicfree.free.module.core.common.util;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.util.function.Function;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;

/**
 * @author zengzhifei
 * @date 2023/3/22 19:32
 */
public class LambdaUtils {
    public static <T extends Function<?, ?> & Serializable> String getMethodName(T func) {
        SerializedLambda serializedLambda = ReflectUtil.invoke(func, "writeReplace");
        return serializedLambda.getImplMethodName();
    }

    public static <T extends Function<?, ?> & Serializable> String getFieldName(T func) {
        final String methodName = getMethodName(func);
        if (methodName.startsWith("get") || methodName.startsWith("set")) {
            return StrUtil.removePreAndLowerFirst(methodName, 3);
        } else if (methodName.startsWith("is")) {
            return StrUtil.removePreAndLowerFirst(methodName, 2);
        } else {
            throw new IllegalArgumentException("Invalid Getter or Setter name: " + methodName);
        }
    }
}
