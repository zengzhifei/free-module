package cc.flyfree.free.module.core.common.aop.advice;

import java.util.function.Consumer;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import cc.flyfree.free.module.core.common.domain.Result;
import cc.flyfree.free.module.core.common.gson.GsonUtil;
import cc.flyfree.free.module.core.common.exception.BizException;
import cc.flyfree.free.module.core.common.support.StopWatcher;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zengzhifei
 * @date 2022/8/15 12:44
 */
@Slf4j
public abstract class AbstractInterfaceLogAdvice {
    private Consumer<Throwable> exceptionConsumer;

    public void setExceptionConsumer(Consumer<Throwable> exceptionConsumer) {
        this.exceptionConsumer = exceptionConsumer;
    }

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
            if (exceptionConsumer != null) {
                exceptionConsumer.accept(e);
            }
            return Result.fail(e.getMessage());
        }
    }

    public abstract Object advice(ProceedingJoinPoint jp);
}
