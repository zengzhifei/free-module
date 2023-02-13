package com.stoicfree.free.es.module.domain;

import lombok.Data;

/**
 * @author zengzhifei
 * @date 2022/9/27 15:18
 */
@Data
public class Nested<T> {
    private T element;

    private Nested(T element) {
        this.element = element;
    }

    public static <T> Nested<T> of(T element) {
        return new Nested<>(element);
    }
}
