package cc.flyfree.free.module.core.common.gson;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.ToNumberPolicy;
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
    private static final Gson GSON;
    public static final Gson JSON;

    private GsonUtil() {
    }

    static {
        // 默认:当字段值为空或null时，依然对该字段进行转换
        GSON = build(GsonBuilder::serializeNulls);
        // 原生gson
        JSON = build();
    }

    public static String toJson(Object src) {
        return GSON.toJson(src);
    }

    public static JsonObject toJsonObject(Object src) {
        return GSON.toJsonTree(src).getAsJsonObject();
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        return GSON.fromJson(json, classOfT);
    }

    public static <T> T fromJson(String json, Type typeOfT) {
        return GSON.fromJson(json, typeOfT);
    }

    public static <T> T fromJson(String json, Class<?> rawClazz, Class<?>... genericClasses) {
        Type type = TypeToken.getParameterized(rawClazz, genericClasses).getType();
        return GSON.fromJson(json, type);
    }

    public static <T> List<T> fromJsonToList(String json, Class<T> clazz) {
        Type type = TypeToken.getParameterized(List.class, clazz).getType();
        return GSON.fromJson(json, type);
    }

    public static <K, V> Map<K, V> fromJsonToMap(String json, Class<K> keyClazz, Class<V> valueClazz) {
        Type type = TypeToken.getParameterized(Map.class, keyClazz, valueClazz).getType();
        return GSON.fromJson(json, type);
    }

    public static Map<String, Object> objectToMap(Object src, boolean getNulls) {
        if (src == null) {
            return null;
        }

        Field[] fields = ReflectUtil.getFieldsDirectly(src.getClass(), true);
        Map<String, Object> result = new HashMap<>(fields.length);

        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(src);
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

    public static JsonObject emptyJsonObject() {
        return new JsonObject();
    }

    public static String emptyJson() {
        return toJson(emptyJsonObject());
    }

    public static Gson build() {
        return build(null);
    }

    public static Gson build(Consumer<GsonBuilder> consumer) {
        // 配置基础属性
        GsonBuilder gsonBuilder = new GsonBuilder()
                // 有 transient 和 static 修饰的字段 不做序列化处理
                .excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.STATIC)
                // 当有GsonIgnore注解时,过滤该字段
                .setExclusionStrategies(new GsonIgnoreExclusionStrategy())
                // 当Map的key为复杂对象时,需要开启该方法
                .enableComplexMapKeySerialization()
                // 时间转化为特定格式
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                // 当key为class类型时,需要开启该方法
                .registerTypeAdapter(Class.class, new ClassCodec())
                // 防止特殊字符出现乱码
                .disableHtmlEscaping();

        // 配置额外属性
        if (consumer != null) {
            consumer.accept(gsonBuilder);
        }

        // 构建
        Gson gson = gsonBuilder.create();

        // 通过反射修改gson 内部实现的ObjectTypeAdapter 方法，避免修改了源代码
        try {
            Field factories = Gson.class.getDeclaredField("factories");
            factories.setAccessible(true);
            Object o = factories.get(gson);
            Class<?>[] declaredClasses = Collections.class.getDeclaredClasses();
            for (Class<?> c : declaredClasses) {
                if ("java.util.Collections$UnmodifiableList".equals(c.getName())) {
                    Field listField = c.getDeclaredField("list");
                    listField.setAccessible(true);
                    @SuppressWarnings("unchecked")
                    List<TypeAdapterFactory> list = (List<TypeAdapterFactory>) listField.get(o);
                    int i = list.indexOf(ObjectTypeAdapter.getFactory(ToNumberPolicy.DOUBLE));
                    list.set(i, GsonObjectTypeAdapter.FACTORY);
                    break;
                }
            }
        } catch (Exception e) {
            log.error("GSON init error:", e);
        }

        return gson;
    }
}
