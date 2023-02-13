package com.stoicfree.free.es.module.service.impl;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.Strings;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import com.google.common.collect.Maps;
import com.stoicfree.free.common.module.enums.ErrorCode;
import com.stoicfree.free.common.module.gson.GsonUtil;
import com.stoicfree.free.common.module.support.BizException;
import com.stoicfree.free.common.module.support.Safes;
import com.stoicfree.free.common.module.support.StopWatcher;
import com.stoicfree.free.es.module.domain.EsPage;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zengzhifei
 * @date 2022/8/12 16:29
 */
@Slf4j
public class AbstractEsService {
    protected Set<String> handleBulkRequest(RestHighLevelClient client, BulkRequest request, int retries) {
        StopWatcher watcher = new StopWatcher();
        BulkResponse response = null;
        for (int i = 0; i < retries; i++) {
            try {
                response = client.bulk(request, RequestOptions.DEFAULT);
                break;
            } catch (Exception e) {
                log.error("es handlerBulkRequest exception times: {}", i, e);
            }
        }

        log.info("es bulk request = {}, response = {}, cost = {}", request.requests(),
                Safes.of(response, Strings::toString), watcher.end());

        if (response == null) {
            throw new BizException(ErrorCode.IO_EXCEPTION);
        }

        return handleBulkResponse(response);

    }

    protected <R> Map<String, R> handleGetRequest(RestHighLevelClient client, MultiGetRequest request,
                                                  Class<R> responseClass, int retries) {
        Map<String, R> entities = Maps.newHashMap();

        StopWatcher watcher = new StopWatcher();
        MultiGetResponse response = null;
        for (int i = 0; i < retries; i++) {
            try {
                response = client.mget(request, RequestOptions.DEFAULT);
                break;
            } catch (Exception e) {
                log.error("es handleGetRequest exception times: {}", i, e);
            }
        }

        log.info("es get request = {}, response = {}, cost = {}", request.getItems(),
                Safes.of(response, Strings::toString), watcher.end());

        if (response == null) {
            return entities;
        }

        MultiGetItemResponse[] responses = response.getResponses();
        for (MultiGetItemResponse item : responses) {
            GetResponse getResponse = item.getResponse();
            String sourceAsString = getResponse.getSourceAsString();
            if (StringUtils.isBlank(sourceAsString)) {
                continue;
            }
            try {
                R entity = GsonUtil.fromJson(sourceAsString, responseClass);
                if (null != entity) {
                    entities.put(item.getId(), entity);
                }
            } catch (Exception e) {
                log.error("es handleGetRequest convert error, source: {}", sourceAsString, e);
            }
        }

        return entities;
    }

    protected <R> EsPage<R> handleSearchRequest(RestHighLevelClient client, SearchRequest request,
                                                Class<R> responseClass, int retries) {
        EsPage<R> page = new EsPage<>();

        StopWatcher watcher = new StopWatcher();
        SearchResponse response = null;
        for (int i = 0; i < retries; i++) {
            try {
                response = client.search(request, RequestOptions.DEFAULT);
                break;
            } catch (Exception e) {
                log.error("es handleSearchRequest exception times: {}", i, e);
            }
        }
        log.info("es search request = {}, response = {}, cost = {}", request.source(), response, watcher.end());

        if (response == null) {
            return page;
        }

        LinkedHashMap<String, R> entities = Maps.newLinkedHashMap();
        SearchHits hits = response.getHits();
        for (SearchHit hit : hits) {
            String sourceAsString = hit.getSourceAsString();
            if (StringUtils.isBlank(sourceAsString)) {
                continue;
            }
            try {
                R entity = GsonUtil.fromJson(sourceAsString, responseClass);
                if (null != entity) {
                    entities.put(hit.getId(), entity);
                }
            } catch (Exception e) {
                log.error("es handleSearchRequest convert error, source: {}", sourceAsString, e);
            }
        }

        page.setTotal(hits.getTotalHits().value);
        page.setItems(entities);

        return page;
    }

    private Set<String> handleBulkResponse(BulkResponse bulkResponse) {
        Set<String> fails = new HashSet<>();
        if (!bulkResponse.hasFailures()) {
            return fails;
        }

        BulkItemResponse[] itemResponse = bulkResponse.getItems();
        for (BulkItemResponse res : itemResponse) {
            if (res.isFailed()) {
                fails.add(res.getId());
                log.error("es handleBulkResponse fail id: {} msg: {}", res.getId(), res.getFailureMessage());
            }
        }
        return fails;
    }
}
