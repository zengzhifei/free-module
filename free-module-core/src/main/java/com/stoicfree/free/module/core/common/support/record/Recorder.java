package com.stoicfree.free.module.core.common.support.record;

import java.util.function.Function;

import javax.annotation.PostConstruct;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stoicfree.free.module.core.common.enums.ErrorCode;
import com.stoicfree.free.module.core.common.support.Assert;
import com.stoicfree.free.module.core.common.util.InstanceUtils;
import com.stoicfree.free.module.core.common.util.ReflectionUtils;

import cn.hutool.core.date.DateUtil;

/**
 * @author zengzhifei
 * @date 2023/3/17 23:25
 */
public class Recorder<E> {
    private final BaseMapper<E> mapper;
    private final RecordColumn<E> column;
    private final Class<E> entityClass;

    public Recorder(BaseMapper<E> mapper, RecordColumn<E> column, Class<E> entityClass) {
        this.mapper = mapper;
        this.column = column;
        this.entityClass = entityClass;
    }

    @PostConstruct
    private void init() {
        Assert.notNull(mapper, ErrorCode.INVALID_PARAMS, "mapper not be null");
        Assert.notNull(column, ErrorCode.INVALID_PARAMS, "column not be null");
        Assert.allFieldNotNull(column, ErrorCode.INVALID_PARAMS, "%s not be null");
        Assert.notNull(entityClass, ErrorCode.INVALID_PARAMS, "entityClass not be null");
    }

    public boolean record(Integer type, String action, String content) {
        return record("", type, action, content);
    }

    public boolean record(String mainId, Integer type, String action, String content) {
        return record(mainId, type, action, content, "");
    }

    public boolean record(String mainId, Integer type, String action, String content, String ext) {
        return record(mainId, type, action, content, ext, "");
    }

    public boolean record(String mainId, Integer type, String action, String content, String ext, String user) {
        E entity = InstanceUtils.newInstance(entityClass);

        ReflectionUtils.setFieldValue(entity, fn(column.getMainId()), mainId);
        ReflectionUtils.setFieldValue(entity, fn(column.getType()), type);
        ReflectionUtils.setFieldValue(entity, fn(column.getAction()), action);
        ReflectionUtils.setFieldValue(entity, fn(column.getContent()), content);
        ReflectionUtils.setFieldValue(entity, fn(column.getExt()), ext);
        ReflectionUtils.setFieldValue(entity, fn(column.getUser()), user);
        ReflectionUtils.setFieldValue(entity, fn(column.getTime()), DateUtil.current());

        return mapper.insert(entity) >= 1;
    }

    private String fn(Function<E, ?> filed) {
        return ReflectionUtils.getFieldName(filed);
    }
}
