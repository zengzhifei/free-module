package cc.flyfree.free.module.core.common.util;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cc.flyfree.free.module.core.common.support.Func;

/**
 * @author zengzhifei
 * @date 2023/3/10 10:27
 */
public class EnumUtils {
    public static <E extends Enum<E>, T> boolean in(Func<E, T> function, T value) {
        Class<E> enumClass = LambdaUtils.getRealClass(function);
        E[] enumConstants = enumClass.getEnumConstants();
        return Arrays.stream(enumConstants).anyMatch(e -> function.apply(e).equals(value));
    }

    public static <E extends Enum<E>, T> E of(Func<E, T> function, T value) {
        Class<E> enumClass = LambdaUtils.getRealClass(function);
        E[] enumConstants = enumClass.getEnumConstants();
        return Arrays.stream(enumConstants).filter(e -> function.apply(e).equals(value)).findFirst().orElse(null);
    }

    public static <E extends Enum<E>, T> List<T> toList(Func<E, T> function) {
        Class<E> enumClass = LambdaUtils.getRealClass(function);
        E[] enumConstants = enumClass.getEnumConstants();
        return Arrays.stream(enumConstants).map(function).collect(Collectors.toList());
    }

    public static <E extends Enum<E>, K, V> Map<K, V> toMap(Func<E, K> keyFunc, Func<E, V> valueFunc) {
        Class<E> enumClass = LambdaUtils.getRealClass(keyFunc);
        E[] enumConstants = enumClass.getEnumConstants();
        return Arrays.stream(enumConstants).collect(Collectors.toMap(keyFunc, valueFunc, (o, d) -> d));
    }

    public static <E extends Enum<E>, K, V> Map<K, List<V>> groupBy(Func<E, K> keyFunc, Func<E, V> valueFunc) {
        Class<E> enumClass = LambdaUtils.getRealClass(keyFunc);
        E[] enumConstants = enumClass.getEnumConstants();
        return Arrays.stream(enumConstants).collect(Collectors.groupingBy(keyFunc,
                Collectors.mapping(valueFunc, Collectors.toList())));
    }

    public static <E extends Enum<E>, K, V> List<V> by(Func<E, K> keyFunc, K key, Func<E, V> valueFunc) {
        return groupBy(keyFunc, valueFunc).get(key);
    }
}
