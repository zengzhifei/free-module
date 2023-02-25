package com.stoicfree.free.module.core.common.support;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zengzhifei
 * @date 2022/8/14 17:21
 */
@Slf4j
public class Column<T, I> {
    private final Map<String, I> columnMap = new HashMap<>();

    @SafeVarargs
    public static <T> Column<T, ?> build(Function<T, ?>... fns) {
        Column<T, ?> column = new Column<>();
        if (fns != null && fns.length > 0) {
            for (Function<T, ?> fn : fns) {
                column.add(fn);
            }
        }
        return column;
    }

    public Column<T, I> add(String column) {
        this.add(column, null);
        return this;
    }

    public Column<T, I> add(String column, I info) {
        columnMap.put(column, info);
        return this;
    }

    public Column<T, I> add(Function<T, ?> fn) {
        this.add(fn, null);
        return this;
    }

    public Column<T, I> add(Function<T, ?> fn, I info) {
        try {
            Method method = fn.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(true);
            SerializedLambda serializedLambda = (SerializedLambda) method.invoke(fn);
            String fieldWithGet = serializedLambda.getImplMethodName();
            // 转小驼峰
            char[] chars = fieldWithGet.substring(3).toCharArray();
            chars[0] = Character.toLowerCase(chars[0]);
            columnMap.put(new String(chars), info);
        } catch (ReflectiveOperationException e) {
            log.warn("Column ReflectiveOperationException", e);
        }
        return this;
    }

    public I getColumn(String name) {
        return this.columnMap.get(name);
    }

    public Map<String, I> getColumns() {
        return this.columnMap;
    }

    public Set<String> names() {
        return this.columnMap.keySet();
    }
}
