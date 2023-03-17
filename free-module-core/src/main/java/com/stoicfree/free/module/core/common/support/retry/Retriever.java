package com.stoicfree.free.module.core.common.support.retry;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stoicfree.free.module.core.common.config.RetryProperties;
import com.stoicfree.free.module.core.common.enums.ErrorCode;
import com.stoicfree.free.module.core.common.support.AnnotatedBeanContainer;
import com.stoicfree.free.module.core.common.support.Assert;
import com.stoicfree.free.module.core.common.support.TwoTuple;
import com.stoicfree.free.module.core.common.util.InstanceUtils;
import com.stoicfree.free.module.core.common.util.ReflectionUtils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import lombok.extern.slf4j.Slf4j;

/**
 * retriever
 *
 * @author zengzhifei
 * @date 2023/3/16 16:02
 */
@Slf4j
public class Retriever<E> extends AnnotatedBeanContainer {
    private final Map<Integer, TwoTuple<Object, Method>> handlers = new HashMap<>();
    private final BaseMapper<E> mapper;
    private final RetryColumn<E> column;
    private final Class<?> entityClass;
    private final RetryProperties properties;

    public Retriever(BaseMapper<E> mapper, RetryColumn<E> column, Class<E> entityClass, RetryProperties properties) {
        super(null, RetryHandler.class);

        this.mapper = mapper;
        this.column = column;
        this.entityClass = entityClass;
        this.properties = properties;
    }

    @PostConstruct
    private void init() {
        Assert.notNull(mapper, ErrorCode.INVALID_PARAMS, "mapper not be null");
        Assert.notNull(column, ErrorCode.INVALID_PARAMS, "column not be null");
        Assert.allFieldNotNull(column, ErrorCode.INVALID_PARAMS, "%s not be null");
        Assert.notNull(entityClass, ErrorCode.INVALID_PARAMS, "entityClass not be null");
    }

    public boolean initTask(Integer type, String content) {
        return initTask("", type, content);
    }

    public boolean initTask(String mainId, Integer type) {
        return initTask(mainId, type, "");
    }

    public boolean initTask(String mainId, Integer type, String content) {
        return initTask(mainId, type, content, 10);
    }

    public boolean initTask(String mainId, Integer type, String content, Integer maxTimes) {
        return initTask(mainId, type, content, maxTimes, "");
    }

    public boolean initTask(String mainId, Integer type, String content, Integer maxTimes, String ext) {
        E entity = InstanceUtils.newInstance(entityClass);

        ReflectionUtils.setFieldValue(entity, fn(column.getMainId()), mainId);
        ReflectionUtils.setFieldValue(entity, fn(column.getType()), type);
        ReflectionUtils.setFieldValue(entity, fn(column.getContent()), content);
        ReflectionUtils.setFieldValue(entity, fn(column.getMaxTimes()), maxTimes);
        ReflectionUtils.setFieldValue(entity, fn(column.getRetryTimes()), 0);
        ReflectionUtils.setFieldValue(entity, fn(column.getExt()), ext);
        ReflectionUtils.setFieldValue(entity, fn(column.getStatus()), RetryStatus.PENDING_RETRY.getStatus());
        ReflectionUtils.setFieldValue(entity, fn(column.getCreateTime()), DateUtil.current());
        ReflectionUtils.setFieldValue(entity, fn(column.getUpdateTime()), DateUtil.current());

        return mapper.insert(entity) >= 1;
    }

    public boolean cancelTask(Long taskId) {
        return updateRetrieveTask(taskId, () -> {
            E entity = InstanceUtils.newInstance(entityClass);
            ReflectionUtils.setFieldValue(entity, fn(column.getStatus()), RetryStatus.RETRY_CANCEL.getStatus());
            return entity;
        });
    }

    public void handleRetryTask(Long taskId) {
        LambdaQueryWrapper<E> queryWrapper = new LambdaQueryWrapper<>();
        if (Objects.nonNull(taskId)) {
            queryWrapper.eq(column.getId(), taskId);
        } else {
            queryWrapper.eq(column.getStatus(), RetryStatus.PENDING_RETRY.getStatus());
        }

        // 查找任务
        List<E> tasks = mapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(tasks)) {
            return;
        }

        for (E task : tasks) {
            try {
                // 获取任务id
                Long id = (Long) ReflectionUtils.getFieldValue(task, fn(column.getId()));
                // 获取任务类型
                Integer type = (Integer) ReflectionUtils.getFieldValue(task, fn(column.getType()));

                // 超过执行次数，结束任务
                Integer maxTimes = (Integer) ReflectionUtils.getFieldValue(task, fn(column.getMaxTimes()));
                Integer retryTimes = (Integer) ReflectionUtils.getFieldValue(task, fn(column.getRetryTimes()));
                if (retryTimes >= maxTimes) {
                    updateRetrieveTask(id, () -> {
                        E entity = InstanceUtils.newInstance(entityClass);
                        ReflectionUtils.setFieldValue(entity, fn(column.getStatus()),
                                RetryStatus.RETRY_FAIL.getStatus());
                        return entity;
                    });
                    continue;
                }

                // 执行任务
                boolean handleResult = handle(type, task);

                // 更新任务状态
                E entity = InstanceUtils.newInstance(entityClass);
                ReflectionUtils.setFieldValue(entity, fn(column.getRetryTimes()), retryTimes + 1);
                if (handleResult) {
                    ReflectionUtils.setFieldValue(entity, fn(column.getStatus()),
                            RetryStatus.RETRY_SUCCESS.getStatus());
                }
                updateRetrieveTask(id, () -> entity);
            } catch (Exception e) {
                log.error("RetryService handleRetryTask error", e);
            }
        }
    }

    @Override
    protected void afterInitAnnotatedBeanContainer() {
        Map<Object, List<Method>> beanMethods = super.getAnnotatedBeanMethods();
        for (Map.Entry<Object, List<Method>> entry : beanMethods.entrySet()) {
            Object bean = entry.getKey();
            List<Method> methods = entry.getValue();
            for (Method method : methods) {
                RetryHandler retryHandler = method.getDeclaredAnnotation(RetryHandler.class);
                int type = retryHandler.type();
                if (handlers.containsKey(type)) {
                    log.warn("RetryService RetryHandler type {} need required one", type);
                }
                handlers.put(type, TwoTuple.of(bean, method));
            }
        }

        // 开启定时任务
        CronUtil.schedule(properties.getCrontab(), (Task) () -> handleRetryTask(null));
        CronUtil.start(true);
    }

    private boolean updateRetrieveTask(Long taskId, Supplier<E> supplier) {
        E entity = supplier.get();
        ReflectionUtils.setFieldValue(entity, fn(column.getUpdateTime()), DateUtil.current());

        LambdaUpdateWrapper<E> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(column.getId(), taskId);

        return mapper.update(entity, updateWrapper) >= 1;
    }

    private boolean handle(Integer type, E task) {
        TwoTuple<Object, Method> beanMethodPair = handlers.get(type);
        if (beanMethodPair == null) {
            log.error("RetryService RetryHandler type {} is not found", type);
            return false;
        }

        try {
            Object bean = beanMethodPair.getFirst();
            Method method = beanMethodPair.getSecond();
            Object result = method.invoke(bean, task);
            if (result instanceof Boolean) {
                return (Boolean) result;
            } else {
                return true;
            }
        } catch (Exception e) {
            log.error("RetryService handle error, type = {}, task = {}", type, task, e);
            return false;
        }
    }

    private String fn(Function<E, ?> filed) {
        return ReflectionUtils.getFieldName(filed);
    }
}
