package com.stoicfree.free.mvc.module.security.anotation.advice;

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

import com.stoicfree.free.common.module.util.AopUtils;
import com.stoicfree.free.mvc.module.security.anotation.Login;
import com.stoicfree.free.mvc.module.security.service.SecurityUserService;

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
    private SecurityUserService<?> securityUserService;

    @Before("@annotation(com.stoicfree.free.mvc.module.security.anotation.Login)")
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
            securityUserService.login(username, password, request, response);
        }
    }
}
