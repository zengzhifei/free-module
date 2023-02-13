package com.stoicfree.free.es.module.util;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;

import com.stoicfree.free.es.module.annotation.EsQuery;
import com.stoicfree.free.es.module.domain.Nested;
import com.stoicfree.free.es.module.domain.Range;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zengzhifei
 * @date 2022/8/12 19:23
 */
@Slf4j
public class EsQueryBuilder {
    public static <T> BoolQueryBuilder buildBoolQuery(T query) {
        return buildBoolQuery(null, query);
    }

    private static <T> BoolQueryBuilder buildBoolQuery(String parentName, T query) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        Field[] fields = query.getClass().getDeclaredFields();
        for (Field field : fields) {
            // 解析字段
            Object value = null;
            try {
                field.setAccessible(true);
                value = field.get(query);
            } catch (Exception e) {
                log.error("EsQueryBuilder buildBoolQuery error, {}", query, e);
            }
            if (value == null) {
                continue;
            }

            // 解析注解
            EsQuery esQuery = field.getDeclaredAnnotation(EsQuery.class);
            String name = field.getName();
            String wildcard = null;
            EsQuery.Clause clause = EsQuery.Clause.MUST;
            if (esQuery != null) {
                if (StringUtils.isNotBlank(esQuery.name())) {
                    name = esQuery.name();
                }
                if (StringUtils.isNotBlank(esQuery.wildcard())) {
                    wildcard = esQuery.wildcard();
                }
                clause = esQuery.clause();
            }

            name = Stream.of(parentName, name).filter(Objects::nonNull).collect(Collectors.joining("."));

            // 获取字段类型
            Class<?> fieldType = field.getType();

            // 过滤特殊类型
            if (fieldType.isArray()) {
                continue;
            }

            // 构建Query
            List<QueryBuilder> queryBuilders;
            switch (clause) {
                case MUST_NOT:
                    queryBuilders = boolQuery.mustNot();
                    break;
                case SHOULD:
                    queryBuilders = boolQuery.should();
                    break;
                case FILTER:
                    queryBuilders = boolQuery.filter();
                    break;
                case MUST:
                default:
                    queryBuilders = boolQuery.must();
            }

            if (ClassUtils.isAssignable(fieldType, Character.class, true)) {
                // 字符类型
                queryBuilders.add(QueryBuilders.termQuery(name, value));
            } else if (ClassUtils.isAssignable(fieldType, String.class, true)) {
                // 字符串类型查询
                if (StringUtils.isBlank((String) value)) {
                    continue;
                }
                // wildcard模糊查询
                if (StringUtils.isNotBlank(wildcard)) {
                    String newValue = wildcard.replace("{}", (String) value);
                    queryBuilders.add(QueryBuilders.wildcardQuery(name, newValue));
                } else {
                    // 字符串精确查询
                    queryBuilders.add(QueryBuilders.termQuery(name, value));
                }
            } else if (ClassUtils.isAssignable(fieldType, Boolean.class, true)) {
                // bool精确查询
                queryBuilders.add(QueryBuilders.termQuery(name, value));
            } else if (ClassUtils.isAssignable(fieldType, Number.class, true)) {
                // 数字精确查询
                queryBuilders.add(QueryBuilders.termQuery(name, value));
            } else if (ClassUtils.isAssignable(fieldType, Range.class, true)) {
                // 数字范围区间查询
                Range<?> values = (Range<?>) value;
                Number min = values.getMin();
                Number max = values.getMax();
                if (Objects.isNull(min) && Objects.isNull(max)) {
                    continue;
                }
                RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(name);
                if (Objects.nonNull(min)) {
                    rangeQueryBuilder.gte(min);
                }
                if (Objects.nonNull(max)) {
                    rangeQueryBuilder.lte(max);
                }
                queryBuilders.add(rangeQueryBuilder);
            } else if (ClassUtils.isAssignable(fieldType, Collection.class, true)) {
                // 集合范围查询
                if (CollectionUtils.isEmpty((Collection<?>) value)) {
                    continue;
                }
                queryBuilders.add(QueryBuilders.termsQuery(name, (Collection<?>) value));
            } else if (ClassUtils.isAssignable(fieldType, Nested.class, true)) {
                // nested对象查询
                Nested<?> nestedValue = (Nested<?>) value;
                Object element = nestedValue.getElement();
                if (Objects.isNull(element)) {
                    continue;
                }
                queryBuilders.add(QueryBuilders.nestedQuery(name, buildBoolQuery(name, element), ScoreMode.None));
            }
        }
        return boolQuery;
    }
}
