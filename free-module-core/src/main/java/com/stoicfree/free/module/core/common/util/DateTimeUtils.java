package com.stoicfree.free.module.core.common.util;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zengzhifei
 * @date 2022/10/18 16:39
 */
public class DateTimeUtils {
    public static <T> boolean isIntersect(Collection<T> cols, Function<T, Long> start, Function<T, Long> end) {
        if (cols == null || cols.size() <= 1) {
            return false;
        }
        List<T> sortCols = cols.stream().sorted(Comparator.comparing(start)).collect(Collectors.toList());
        Long previous = end.apply(sortCols.get(0));
        for (int i = 1; i < sortCols.size(); i++) {
            Long next = start.apply(sortCols.get(i));
            if (next <= previous) {
                return true;
            }
            previous = end.apply(sortCols.get(i));
        }
        return false;
    }
}
