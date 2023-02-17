package com.stoicfree.free.mvc.module.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import com.stoicfree.free.mvc.module.security.service.UserLoginService;

/**
 * @author zengzhifei
 * @date 2023/2/17 11:16
 */
public class SecurityInterceptor implements HandlerInterceptor {
    @Autowired
    private UserLoginService<?> userLoginService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        userLoginService.verifyLogin(request, response);
        return true;
    }
}
