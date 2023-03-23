package com.stoicfree.free.module.core.common.util;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.util.function.Function;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;

/**
 * @author zengzhifei
 * @date 2023/3/22 19:32
 */
public class LambdaUtils {
    public static <T, R, F extends Function<T, R> & Serializable> Class<T> getRealClass(F func) {
        SerializedLambda lambda = resolve(func);
        checkLambdaTypeCanGetClass(lambda.getImplMethodKind());
        String instantiatedMethodType = lambda.getInstantiatedMethodType();
        return ClassUtil.loadClass(StrUtil.sub(instantiatedMethodType, 2,
                StrUtil.indexOf(instantiatedMethodType, ';')));
    }

    public static <F extends Function<?, ?> & Serializable> String getMethodName(F func) {
        SerializedLambda lambda = resolve(func);
        return lambda.getImplMethodName();
    }

    public static <F extends Function<?, ?> & Serializable> String getFieldName(F func) {
        final String methodName = getMethodName(func);
        if (methodName.startsWith("get") || methodName.startsWith("set")) {
            return StrUtil.removePreAndLowerFirst(methodName, 3);
        } else if (methodName.startsWith("is")) {
            return StrUtil.removePreAndLowerFirst(methodName, 2);
        } else {
            throw new IllegalArgumentException("Invalid Getter or Setter name: " + methodName);
        }
    }

    public static <F extends Function<?, ?> & Serializable> SerializedLambda resolve(F func) {
        return ReflectUtil.invoke(func, "writeReplace", new Object[0]);
    }

    private static void checkLambdaTypeCanGetClass(int implMethodKind) {
        if (implMethodKind != 5 && implMethodKind != 6) {
            throw new IllegalArgumentException("该lambda不是合适的方法引用");
        }
    }
}
