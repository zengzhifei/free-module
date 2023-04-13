package cc.flyfree.free.module.core.mvc.captcha.anotation.advice;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import cc.flyfree.free.module.core.common.util.AopUtils;
import cc.flyfree.free.module.core.mvc.captcha.anotation.VerifyCaptcha;
import cc.flyfree.free.module.core.mvc.captcha.Captcha;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zengzhifei
 * @date 2023/2/19 23:17
 */
@Slf4j
@Order(0)
@Aspect
public class VerifyCaptchaAdvice {
    @Autowired
    private Captcha captcha;

    @Before("@annotation(cc.flyfree.free.module.core.mvc.captcha.anotation.VerifyCaptcha)")
    public void beforeAdvice(JoinPoint joinPoint) {
        // 获取参数
        Object[] args = joinPoint.getArgs();
        // 获取调用方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 获取调用方法
        Method method = signature.getMethod();
        // 获取注解
        VerifyCaptcha annotation = method.getAnnotation(VerifyCaptcha.class);

        // 解析注解
        if (annotation != null) {
            // 获取code
            String captchaCodeKey = annotation.captchaCode();

            // 解析SpEL表达式
            String captchaCode = AopUtils.parseAnnotationParam(captchaCodeKey, method, args, String.class);
            ServletRequestAttributes attribute = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attribute.getRequest();
            // 校验登录
            captcha.verify(captchaCode, request);
        }
    }
}
