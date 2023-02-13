package com.stoicfree.free.es.module.domain;

import lombok.Data;

/**
 * @author zengzhifei
 * @date 2022/8/13 11:01
 */
@Data
public class Range<T extends Number> {
    private T min;
    private T max;

    private Range(T min, T max) {
        this.min = min;
        this.max = max;
    }

    public static <T extends Number> Range<T> of(T min, T max) {
        return new Range<>(min, max);
    }

    public static <T extends Number> Range<T> min(T min) {
        return new Range<>(min, null);
    }

    public static <T extends Number> Range<T> max(T max) {
        return new Range<>(null, max);
    }
}
