package com.stoicfree.free.module.core.common.support;

import lombok.Getter;

/**
 * @author zengzhifei
 * @date 2023/3/17 10:06
 */
public class TwoTuple<A, B> {
    @Getter
    private final A first;
    @Getter
    private final B second;

    public TwoTuple(A first, B second) {
        this.first = first;
        this.second = second;
    }

    public static <A, B> TwoTuple<A, B> of(A first, B second) {
        return new TwoTuple<>(first, second);
    }
}
