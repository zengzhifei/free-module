package cc.flyfree.free.module.core.elastic.service;

import java.util.Collection;
import java.util.Map;

import cc.flyfree.free.module.core.elastic.domain.EsPage;
import cc.flyfree.free.module.core.elastic.domain.EsPageParam;

/**
 * @author zengzhifei
 * @date 2022/8/12 15:32
 */
public interface EsQueryService<Q, R> {
    /**
     * 根据id查询单条数据
     *
     * @param id
     *
     * @return
     */
    R queryById(String id);

    /**
     * 根据id查询批量数据
     *
     * @param ids
     *
     * @return
     */
    Map<String, R> queryByIds(Collection<String> ids);

    /**
     * 根据条件批量查询
     *
     * @param query
     * @param esPageParam
     *
     * @return
     */
    EsPage<R> search(Q query, EsPageParam esPageParam);
}
