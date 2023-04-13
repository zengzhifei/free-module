package cc.flyfree.free.module.core.jdbc.sharding;

import com.baomidou.mybatisplus.annotation.TableName;
import cc.flyfree.free.module.core.jdbc.annotation.TableSharding;

/**
 * @author zengzhifei
 * @date 2023/4/7 17:22
 */
public abstract class SharingStrategy<T, R> {
    protected Class<T> entityClass;
    protected TableSharding tableSharding;

    public SharingStrategy(Class<T> entityClass, TableSharding tableSharding) {
        this.entityClass = entityClass;
        this.tableSharding = tableSharding;
    }

    public String getTableName() {
        TableName annotation = entityClass.getDeclaredAnnotation(TableName.class);
        return annotation.value();
    }

    public TableSharding getTableSharding() {
        return tableSharding;
    }

    /**
     * sharing方法
     *
     * @param value
     *
     * @return
     */
    public abstract String sharding(R value);
}
