package com.stoicfree.free.module.core.common.gson;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.bind.ObjectTypeAdapter;
import com.google.gson.reflect.TypeToken;

import cn.hutool.core.util.ReflectUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zengzhifei
 * @date 2023/2/9 15:26
 */
@Slf4j
public class GsonUtil {
    public static final Gson JSON = new Gson();
    private static final Gson GSON;

    private GsonUtil() {
    }

    static {
        GSON = new GsonBuilder()
                // 有 transient 和 static 修饰的字段 不做序列化处理
                .excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.STATIC)
                // 当有GsonIgnore注解时,过滤该字段
                .setExclusionStrategies(new GsonIgnoreExclusionStrategy())
                // 当Map的key为复杂对象时,需要开启该方法
                .enableComplexMapKeySerialization()
                // 当字段值为空或null时，依然对该字段进行转换
                .serializeNulls()
                // 时间转化为特定格式
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                // 防止特殊字符出现乱码
                .disableHtmlEscaping()
                .create();
        // 通过反射修改gson 内部实现的ObjectTypeAdapter 方法，避免修改了源代码
        try {
            Field factories = Gson.class.getDeclaredField("factories");
            factories.setAccessible(true);
            Object o = factories.get(GSON);
            Class<?>[] declaredClasses = Collections.class.getDeclaredClasses();
            for (Class<?> c : declaredClasses) {
                if ("java.util.Collections$UnmodifiableList".equals(c.getName())) {
                    Field listField = c.getDeclaredField("list");
                    listField.setAccessible(true);
                    @SuppressWarnings("unchecked")
                    List<TypeAdapterFactory> list = (List<TypeAdapterFactory>) listField.get(o);
                    int i = list.indexOf(ObjectTypeAdapter.FACTORY);
                    list.set(i, GsonObjectTypeAdapter.FACTORY);
                    break;
                }
            }
        } catch (Exception e) {
            log.error("GSON init error:", e);
        }
    }

    public static String toJson(Object src) {
        return GSON.toJson(src);
    }

    public static JsonElement toJsonTree(Object src) {
        return GSON.toJsonTree(src);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) throws JsonSyntaxException {
        return GSON.fromJson(json, classOfT);
    }

    public static <T> T fromJson(String json, Type typeOfT) throws JsonSyntaxException {
        return GSON.fromJson(json, typeOfT);
    }

    public static <T> List<T> fromJsonToList(String json, Class<T> clazz) throws JsonSyntaxException {
        Type type = TypeToken.getParameterized(List.class, clazz).getType();
        return GSON.fromJson(json, type);
    }

    public static Map<String, Object> objectToMap(Object object, boolean getNulls) {
        if (object == null) {
            return null;
        }

        Field[] fields = ReflectUtil.getFieldsDirectly(object.getClass(), true);
        Map<String, Object> result = new HashMap<>(fields.length);

        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(object);
                if (!getNulls && value == null) {
                    continue;
                }
                SerializedName serializedName = field.getAnnotation(SerializedName.class);
                if (serializedName != null && StringUtils.isNotBlank(serializedName.value())) {
                    result.put(serializedName.value(), value);
                } else {
                    result.put(field.getName(), value);
                }
            } catch (Exception e) {
                log.warn("GSON objectToMap Error object", e);
            }
        }

        return result;
    }
}
