package com.stoicfree.free.module.core.common.util;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zengzhifei
 * @date 2023/3/10 10:27
 */
public class EnumUtils {
    public static <E extends Enum<E>, T> boolean in(Class<E> enumClass, Function<E, T> function, T value) {
        E[] enumConstants = enumClass.getEnumConstants();
        return Arrays.stream(enumConstants).anyMatch(e -> function.apply(e).equals(value));
    }

    public static <E extends Enum<E>, T> E of(Class<E> enumClass, Function<E, T> function, T value) {
        E[] enumConstants = enumClass.getEnumConstants();
        return Arrays.stream(enumConstants).filter(e -> function.apply(e).equals(value)).findFirst().orElse(null);
    }

    public static <E extends Enum<E>, T> List<T> toList(Class<E> enumClass, Function<E, T> function) {
        E[] enumConstants = enumClass.getEnumConstants();
        return Arrays.stream(enumConstants).map(function).collect(Collectors.toList());
    }

    public static <E extends Enum<E>, K, V> Map<K, V> toMap(Class<E> enumClass, Function<E, K> keyFunc,
                                                            Function<E, V> valueFunc) {
        E[] enumConstants = enumClass.getEnumConstants();
        return Arrays.stream(enumConstants).collect(Collectors.toMap(keyFunc, valueFunc, (o, d) -> d));
    }

    public static <E extends Enum<E>, K, V> Map<K, List<V>> groupBy(Class<E> enumClass, Function<E, K> keyFunc,
                                                                    Function<E, V> valueFunc) {
        E[] enumConstants = enumClass.getEnumConstants();
        return Arrays.stream(enumConstants)
                .collect(Collectors.groupingBy(keyFunc, Collectors.mapping(valueFunc, Collectors.toList())));
    }

    public static <E extends Enum<E>, K, V> List<V> by(Class<E> enumClass, Function<E, K> keyFunc, K key,
                                                       Function<E, V> valueFunc) {
        return groupBy(enumClass, keyFunc, valueFunc).get(key);
    }
}
