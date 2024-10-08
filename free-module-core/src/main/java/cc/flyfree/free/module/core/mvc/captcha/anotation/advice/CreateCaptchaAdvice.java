package cc.flyfree.free.module.core.mvc.captcha.anotation.advice;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import cc.flyfree.free.module.core.mvc.captcha.Captcha;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

    @Before("@annotation(cc.flyfree.free.module.core.mvc.captcha.anotation.CreateCaptcha)")
    public void beforeAdvice(JoinPoint joinPoint) {
        ServletRequestAttributes attribute = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attribute.getRequest();
        HttpServletResponse response = attribute.getResponse();
        captcha.createCode(request, response, true);
    }
}
