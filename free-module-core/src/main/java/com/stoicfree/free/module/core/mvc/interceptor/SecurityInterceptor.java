package com.stoicfree.free.module.core.mvc.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.stoicfree.free.module.core.mvc.security.context.UserContext;
import com.stoicfree.free.module.core.mvc.security.service.SecurityUserService;

/**
 * @author zengzhifei
 * @date 2023/2/17 11:16
 */
public class SecurityInterceptor implements HandlerInterceptor {
    @Autowired
    private SecurityUserService<?> userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        userService.verifyLogin(request, response, handler);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        UserContext.remove();
    }
}
