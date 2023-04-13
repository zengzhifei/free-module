package cc.flyfree.free.module.core.common.support;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cc.flyfree.free.module.core.common.util.LambdaUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zengzhifei
 * @date 2022/8/14 17:21
 */
@Slf4j
public class Column<T, I> {
    private final Map<String, I> columnMap = new HashMap<>();

    @SafeVarargs
    public static <T> Column<T, ?> build(Func<T, ?>... fns) {
        Column<T, ?> column = new Column<>();
        if (fns != null && fns.length > 0) {
            for (Func<T, ?> fn : fns) {
                column.add(fn);
            }
        }
        return column;
    }

    public Column<T, I> add(String column) {
        this.add(column, null);
        return this;
    }

    public Column<T, I> add(String column, I info) {
        columnMap.put(column, info);
        return this;
    }

    public Column<T, I> add(Func<T, ?> fn) {
        this.add(fn, null);
        return this;
    }

    public Column<T, I> add(Func<T, ?> fn, I info) {
        columnMap.put(LambdaUtils.getFieldName(fn), info);
        return this;
    }

    public I getColumn(String name) {
        return this.columnMap.get(name);
    }

    public Map<String, I> getColumns() {
        return this.columnMap;
    }

    public Set<String> names() {
        return this.columnMap.keySet();
    }
}
