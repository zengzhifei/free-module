package com.stoicfree.free.module.core.redis.aop.advice;

import java.util.StringJoiner;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

import com.stoicfree.free.module.core.common.enums.ErrorCode;
import com.stoicfree.free.module.core.common.gson.GsonUtil;
import com.stoicfree.free.module.core.common.support.Assert;
import com.stoicfree.free.module.core.redis.aop.RepeatLimit;
import com.stoicfree.free.module.core.redis.client.RedisClient;

import cn.hutool.crypto.SecureUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zengzhifei
 * @date 2022/8/30 17:31
 */
@Slf4j
@Order
@Aspect
public class RepeatLimitAdvice {
    private final String prefix;

    @Autowired
    private RedisClient redisClient;

    public RepeatLimitAdvice() {
        prefix = "repeat-limit:";
    }

    @Before("@annotation(com.stoicfree.free.module.core.redis.aop.RepeatLimit)")
    public void beforeAdvice(JoinPoint jp) {
        boolean lock = true;
        String key = "";
        long requestId = 0;
        try {
            Object[] args = jp.getArgs();
            String api = jp.getTarget().getClass().getSimpleName();
            MethodSignature signature = (MethodSignature) jp.getSignature();
            String method = signature.getName();
            String params = SecureUtil.md5(GsonUtil.toJson(args));

            key = getRepeatLimitKey(api, method, params);
            RepeatLimit limit = signature.getMethod().getAnnotation(RepeatLimit.class);
            long expires = limit.expires() > 0 ? limit.expires() : 10;
            requestId = Thread.currentThread().getId();
            lock = RedisClient.OK.equals(redisClient.lock(key, String.valueOf(requestId), expires * 1000));
        } catch (Throwable e) {
            log.error("RepeatLimitAdvice beforeAdvice", e);
        } finally {
            log.info("RepeatLimitAdvice beforeAdvice lock = {}, key = {}, requestId = {}", lock, key, requestId);
            Assert.isTrue(lock, ErrorCode.FREQUENT_OPERATIONS);
        }
    }

    @After("@annotation(com.stoicfree.free.module.core.redis.aop.RepeatLimit)")
    public void afterAdvice(JoinPoint jp) {
        String key = "";
        Object ret = null;
        long requestId = 0;
        try {
            Object[] args = jp.getArgs();
            String api = jp.getTarget().getClass().getSimpleName();
            MethodSignature signature = (MethodSignature) jp.getSignature();
            String method = signature.getName();
            String params = SecureUtil.md5(GsonUtil.toJson(args));

            key = getRepeatLimitKey(api, method, params);
            requestId = Thread.currentThread().getId();
            ret = redisClient.unlock(key, String.valueOf(requestId));
        } catch (Throwable e) {
            log.error("RepeatLimitAdvice afterAdvice", e);
        } finally {
            log.info("RepeatLimitAdvice afterAdvice unlock = {}, key = {}, requestId = {}", ret, key, requestId);
        }
    }

    private String getRepeatLimitKey(String api, String method, String params) {
        StringJoiner joiner = new StringJoiner("_");
        joiner.add(prefix).add(api).add(method).add(params);
        return joiner.toString();
    }
}
