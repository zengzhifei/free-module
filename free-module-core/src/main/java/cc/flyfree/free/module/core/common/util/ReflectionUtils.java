package cc.flyfree.free.module.core.common.util;

import java.lang.reflect.Field;

/**
 * @author zengzhifei
 * @date 2023/2/17 16:58
 */
public class ReflectionUtils {
    public static <E> Object getFieldValue(E entity, String fieldName) {
        try {
            Field field = entity.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(entity);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <E> void setFieldValue(E entity, String fieldName, Object fieldValue) {
        try {
            Field field = entity.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(entity, fieldValue);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
