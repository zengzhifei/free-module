package cc.flyfree.free.module.core.elastic.service;

import java.util.Collection;
import java.util.Set;

/**
 * @author zengzhifei
 * @date 2022/8/12 15:32
 */
public interface EsSaveService<T> {
    /**
     * 单条插入
     *
     * @param entity
     *
     * @return
     */
    Boolean insert(T entity);

    /**
     * 批量插入
     *
     * @param entities
     *
     * @return
     */
    Set<String> bulkInsert(Collection<T> entities);

    /**
     * 单条更新
     *
     * @param entity
     *
     * @return
     */
    Boolean update(T entity);

    /**
     * 批量更新
     *
     * @param entities
     *
     * @return
     */
    Set<String> bulkUpdate(Collection<T> entities);
}
