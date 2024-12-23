package cc.flyfree.free.module.core.mvc.uploader.anotation.advice;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import cc.flyfree.free.module.core.common.domain.Result;
import cc.flyfree.free.module.core.common.enums.ErrorCode;
import cc.flyfree.free.module.core.mvc.uploader.Uploader;
import cc.flyfree.free.module.core.mvc.uploader.anotation.AutoUploader;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zengzhifei
 * @date 2024/12/23 19:43
 */
@Slf4j
@Order
@Aspect
public class AutoUploaderAdvice {
    @Autowired
    private Uploader uploader;

    @Around("@annotation(cc.flyfree.free.module.core.mvc.uploader.anotation.AutoUploader)")
    public Object around(ProceedingJoinPoint joinPoint) {
        // 获取参数
        Object[] args = joinPoint.getArgs();
        // 获取调用方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 获取调用方法
        Method method = signature.getMethod();
        // 获取注解
        AutoUploader annotation = method.getAnnotation(AutoUploader.class);

        // 解析注解
        if (annotation != null) {
            // 获取bucket
            String name = annotation.name();
            String bucket = annotation.bucket();

            ServletRequestAttributes attribute = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            MultipartHttpServletRequest request = new StandardServletMultipartResolver()
                    .resolveMultipart(attribute.getRequest());
            // 上传
            String file = uploader.upload(bucket, request.getFile(name));
            return Result.ok(file);
        }
        return Result.fail(ErrorCode.UNKNOWN_ERROR.getCode(), ErrorCode.UNKNOWN_ERROR.getMsg());
    }
}
