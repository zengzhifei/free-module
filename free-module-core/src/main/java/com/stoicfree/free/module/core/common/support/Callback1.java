package com.stoicfree.free.module.core.common.support;

/**
 * @author zengzhifei
 * @date 2023/3/31 17:02
 */
@FunctionalInterface
public interface Callback1<P> {
    /**
     * 有参，无返回
     *
     * @param param
     */
    void call(P param);
}