package cc.flyfree.free.module.core.jdbc.util;

import java.util.function.Function;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cc.flyfree.free.module.core.common.domain.Paging;
import cc.flyfree.free.module.core.common.support.Safes;

/**
 * @author zengzhifei
 * @date 2023/4/13 15:59
 */
public class PageHelper {
    public static <T, E> Paging<T> convert(Page<E> page, Function<E, T> converter) {
        return Paging.<T>builder()
                .count(page.getTotal())
                .rows(Safes.of(page.getRecords()).stream().map(converter).collect(Collectors.toList()))
                .build();
    }
}
