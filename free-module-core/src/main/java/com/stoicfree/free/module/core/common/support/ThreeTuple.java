package com.stoicfree.free.module.core.common.support;

import lombok.Getter;

/**
 * @author zengzhifei
 * @date 2023/3/17 10:09
 */
public class ThreeTuple<A, B, C> extends TwoTuple<A, B> {
    @Getter
    private final C third;

    public ThreeTuple(A first, B second, C third) {
        super(first, second);
        this.third = third;
    }

    public static <A, B, C> ThreeTuple<A, B, C> of(A first, B second, C third) {
        return new ThreeTuple<>(first, second, third);
    }
}
