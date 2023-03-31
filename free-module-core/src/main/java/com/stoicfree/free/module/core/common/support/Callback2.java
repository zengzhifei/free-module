package com.stoicfree.free.module.core.common.support;

/**
 * @author zengzhifei
 * @date 2023/3/31 17:02
 */
@FunctionalInterface
public interface Callback2<R> {
    /**
     * 无参 有返回
     *
     * @return
     */
    R call();
}