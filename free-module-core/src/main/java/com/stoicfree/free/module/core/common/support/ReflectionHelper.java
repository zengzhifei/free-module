package com.stoicfree.free.module.core.common.support;

import java.lang.reflect.Field;
import java.util.function.Function;

/**
 * @author zengzhifei
 * @date 2023/2/17 16:58
 */
public class ReflectionHelper<E> {
    public static <E> String getFieldName(Function<E, ?> filed) {
        Column<E, ?> column = Column.build(filed);
        return column.names().iterator().next();
    }

    public static <E> Object getFieldValue(E entity, String fieldName) {
        try {
            Field field = entity.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(entity);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <E> void setFieldValue(E entity, String fieldName, String fieldValue) {
        try {
            Field field = entity.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(entity, fieldValue);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
