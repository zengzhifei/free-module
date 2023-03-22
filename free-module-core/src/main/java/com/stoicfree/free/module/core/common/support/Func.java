package com.stoicfree.free.module.core.common.support;

import java.io.Serializable;
import java.util.function.Function;

@FunctionalInterface
public interface Func<T, R> extends Function<T, R>, Serializable {
}