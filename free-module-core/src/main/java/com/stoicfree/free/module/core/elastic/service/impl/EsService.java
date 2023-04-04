package com.stoicfree.free.module.core.elastic.service.impl;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;

import com.google.common.collect.Lists;
import com.stoicfree.free.module.core.common.enums.ErrorCode;
import com.stoicfree.free.module.core.common.gson.GsonUtil;
import com.stoicfree.free.module.core.common.support.Assert;
import com.stoicfree.free.module.core.common.exception.BizException;
import com.stoicfree.free.module.core.common.support.Safes;
import com.stoicfree.free.module.core.elastic.annotation.EsID;
import com.stoicfree.free.module.core.elastic.config.EsClientConfig;
import com.stoicfree.free.module.core.elastic.domain.EsPage;
import com.stoicfree.free.module.core.elastic.domain.EsPageParam;
import com.stoicfree.free.module.core.elastic.service.EsQueryService;
import com.stoicfree.free.module.core.elastic.service.EsSaveService;
import com.stoicfree.free.module.core.elastic.util.EsQueryBuilder;

/**
 * @author zengzhifei
 * @date 2022/8/12 15:55
 */
public class EsService<T, Q> extends AbstractEsService implements EsSaveService<T>, EsQueryService<Q, T> {
    private final RestHighLevelClient client;
    private final EsClientConfig config;
    private final Class<T> tClass;
    private Field esIdField;

    public EsService(RestHighLevelClient client, EsClientConfig config, Class<T> tClass) {
        this.client = client;
        this.config = config;
        this.tClass = tClass;
        for (Field field : tClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(EsID.class)) {
                esIdField = field;
                esIdField.setAccessible(true);
                break;
            }
        }
        Assert.notNull(esIdField, ErrorCode.IO_EXCEPTION, tClass.getName() + "缺少EsID");
    }

    @Override
    public Boolean insert(T entity) {
        List<T> entities = Collections.singletonList(entity);
        Set<String> fails = bulkInsert(entities);
        return CollectionUtils.isEmpty(fails);
    }

    @Override
    public Set<String> bulkInsert(Collection<T> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return new HashSet<>();
        }

        BulkRequest request = new BulkRequest();
        for (T entity : entities) {
            String id;
            try {
                id = (String) esIdField.get(entity);
            } catch (Exception e) {
                throw new BizException(ErrorCode.IO_EXCEPTION);
            }
            IndexRequest indexRequest = new IndexRequest(config.getIndex()).id(id);
            // 直接插入文档，(如果存在则会全覆盖)
            indexRequest.source(GsonUtil.toJson(entity), XContentType.JSON);
            request.add(indexRequest);
        }

        return this.handleBulkRequest(client, request, config.getRetries());
    }

    @Override
    public Boolean update(T entity) {
        List<T> entities = Collections.singletonList(entity);
        Set<String> fails = bulkUpdate(entities);
        return CollectionUtils.isEmpty(fails);
    }

    @Override
    public Set<String> bulkUpdate(Collection<T> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return new HashSet<>();
        }

        BulkRequest request = new BulkRequest();
        for (T entity : entities) {
            String id;
            try {
                id = (String) esIdField.get(entity);
            } catch (Exception e) {
                throw new BizException(ErrorCode.IO_EXCEPTION);
            }
            UpdateRequest updateRequest = new UpdateRequest(config.getIndex(), id);
            updateRequest.doc(GsonUtil.toJson(entity), XContentType.JSON);
            // 为true，表明无论文档是否存在，脚本都会执行（如果不存在时，会创建一个新的文档）
            updateRequest.scriptedUpsert(true);
            // 如果文档不存在，使用upsert方法，会根据更新内容创建新的文档
            updateRequest.upsert(GsonUtil.toJson(entity), XContentType.JSON);
            request.add(updateRequest);
        }

        return this.handleBulkRequest(client, request, config.getRetries());
    }

    @Override
    public T queryById(String id) {
        Map<String, T> map = queryByIds(Lists.newArrayList(id));
        return map.get(id);
    }

    @Override
    public Map<String, T> queryByIds(Collection<String> ids) {
        MultiGetRequest request = new MultiGetRequest();
        Safes.of(ids).stream().filter(StringUtils::isNotBlank).forEach(
                id -> request.add(new MultiGetRequest.Item(config.getIndex(), id))
        );
        if (CollectionUtils.isEmpty(request.getItems())) {
            return new HashMap<>(0);
        }

        return this.handleGetRequest(client, request, tClass, config.getRetries());
    }

    @Override
    public EsPage<T> search(Q query, EsPageParam pageParam) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 设置分页参数
        searchSourceBuilder.from((pageParam.getPageNo() - 1) * pageParam.getPageSize()).size(pageParam.getPageSize());
        // 设置正向排序
        if (CollectionUtils.isNotEmpty(pageParam.getOrderByAsc())) {
            for (String sortField : pageParam.getOrderByAsc()) {
                searchSourceBuilder.sort(sortField, SortOrder.ASC);
            }
        }
        // 设置反向排序
        if (CollectionUtils.isNotEmpty(pageParam.getOrderByDesc())) {
            for (String sortField : pageParam.getOrderByDesc()) {
                searchSourceBuilder.sort(sortField, SortOrder.DESC);
            }
        }
        // 构建查询参数
        BoolQueryBuilder boolQuery = EsQueryBuilder.buildBoolQuery(query);
        // 设置查询语句
        searchSourceBuilder.query(boolQuery);
        // 构建ES查询对象
        SearchRequest request = new SearchRequest(config.getIndex());
        request.source(searchSourceBuilder);

        EsPage<T> esPage = this.handleSearchRequest(client, request, tClass, config.getRetries());
        return esPage.convert(pageParam);
    }
}
