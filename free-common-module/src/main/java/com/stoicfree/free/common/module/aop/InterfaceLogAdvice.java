package com.stoicfree.free.common.module.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import com.stoicfree.free.common.module.domain.Result;
import com.stoicfree.free.common.module.gson.GsonUtil;
import com.stoicfree.free.common.module.support.BizException;
import com.stoicfree.free.common.module.support.StopWatcher;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zengzhifei
 * @date 2022/8/15 12:44
 */
@Slf4j
public abstract class InterfaceLogAdvice {
    public Object around(ProceedingJoinPoint jp) {
        StopWatcher watcher = new StopWatcher();
        String interfaceName = "";
        try {
            String className = jp.getTarget().getClass().getSimpleName();
            MethodSignature ms = (MethodSignature) jp.getSignature();
            String methodName = ms.getName();
            interfaceName = className.concat("#").concat(methodName);
            log.info("request start, api:{}, parameters:{}", interfaceName, GsonUtil.toJson(jp.getArgs()));
            Object result = jp.proceed();
            log.info("request success, api:{}, parameters:{}, response:{}, cost:{}",
                    interfaceName, GsonUtil.toJson(jp.getArgs()), GsonUtil.toJson(result), watcher.end());
            return result;
        } catch (BizException e) {
            log.warn("request exception, api:{}, parameters:{}, cost:{}",
                    interfaceName, GsonUtil.toJson(jp.getArgs()), watcher.end(), e);
            return Result.fail(e.getCode(), e.getMessage());
        } catch (Throwable e) {
            log.error("request error, api:{}, parameters:{}, cost:{}",
                    interfaceName, GsonUtil.toJson(jp.getArgs()), watcher.end(), e);
            return Result.fail(e.getMessage());
        }
    }

    public abstract Object advice(ProceedingJoinPoint jp);
}
