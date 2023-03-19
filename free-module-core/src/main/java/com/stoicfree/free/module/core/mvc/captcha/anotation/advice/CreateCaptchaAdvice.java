package com.stoicfree.free.module.core.mvc.captcha.anotation.advice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.stoicfree.free.module.core.mvc.captcha.service.Captcha;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zengzhifei
 * @date 2023/2/19 23:17
 */
@Slf4j
@Order
@Aspect
public class CreateCaptchaAdvice {
    @Autowired
    private Captcha captcha;

    @Before("@annotation(com.stoicfree.free.module.core.mvc.captcha.anotation.CreateCaptcha)")
    public void beforeAdvice(JoinPoint joinPoint) {
        ServletRequestAttributes attribute = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attribute.getRequest();
        HttpServletResponse response = attribute.getResponse();
        captcha.createCode(request, response, true);
    }
}
