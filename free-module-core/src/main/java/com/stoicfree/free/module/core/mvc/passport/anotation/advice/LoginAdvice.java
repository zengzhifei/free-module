package com.stoicfree.free.module.core.mvc.passport.anotation.advice;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.stoicfree.free.module.core.common.util.AopUtils;
import com.stoicfree.free.module.core.mvc.passport.anotation.Login;
import com.stoicfree.free.module.core.mvc.passport.service.PassGate;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zengzhifei
 * @date 2023/2/19 23:17
 */
@Slf4j
@Order
@Aspect
public class LoginAdvice {
    @Autowired
    private PassGate<?> passGate;

    @Before("@annotation(com.stoicfree.free.module.core.mvc.passport.anotation.Login)")
    public void beforeAdvice(JoinPoint joinPoint) {
        // 获取参数
        Object[] args = joinPoint.getArgs();
        // 获取调用方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 获取调用方法
        Method method = signature.getMethod();
        // 获取注解
        Login annotation = method.getAnnotation(Login.class);

        // 解析注解
        if (annotation != null) {
            // 获取username
            String usernameKey = annotation.username();
            // 获取password
            String passwordKey = annotation.password();

            // 解析SpEL表达式
            String username = AopUtils.parseAnnotationParam(usernameKey, method, args, String.class);
            String password = AopUtils.parseAnnotationParam(passwordKey, method, args, String.class);
            ServletRequestAttributes attribute = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attribute.getRequest();
            HttpServletResponse response = attribute.getResponse();
            // 校验登录
            passGate.login(username, password, request, response);
        }
    }
}
