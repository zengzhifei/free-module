package com.stoicfree.free.common.module.support;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @author zengzhifei
 * @date 2022/8/12 18:35
 */
public class Safes {
    public static <K, V> Map<K, V> of(Map<K, V> source) {
        return Optional.ofNullable(source).orElse(Maps.newHashMapWithExpectedSize(0));
    }

    public static <T> Iterator<T> of(Iterator<T> source) {
        return Optional.ofNullable(source).orElse(Collections.emptyIterator());
    }

    public static <T> Collection<T> of(Collection<T> source) {
        return Optional.ofNullable(source).orElse(Lists.newArrayListWithCapacity(0));
    }

    public static <T> Iterable<T> of(Iterable<T> source) {
        return Optional.ofNullable(source).orElse(Lists.newArrayListWithCapacity(0));
    }

    public static <T> List<T> of(List<T> source) {
        return Optional.ofNullable(source).orElse(Lists.newArrayListWithCapacity(0));
    }

    public static <T> Set<T> of(Set<T> source) {
        return Optional.ofNullable(source).orElse(Sets.newHashSetWithExpectedSize(0));
    }

    public static BigDecimal of(BigDecimal source) {
        return Optional.ofNullable(source).orElse(BigDecimal.ZERO);
    }

    public static String of(String source) {
        return Optional.ofNullable(source).orElse("");
    }

    public static String of(String source, String defaultStr) {
        return Optional.ofNullable(source).orElse(defaultStr);
    }

    public static <T, R> R of(T object, Function<T, R> mapper) {
        return Optional.ofNullable(object).map(mapper).orElse(null);
    }
}
