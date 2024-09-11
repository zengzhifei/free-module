package cc.flyfree.free.module.core.mvc.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import cc.flyfree.free.module.core.mvc.passport.PassGate;
import cc.flyfree.free.module.core.mvc.passport.context.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author zengzhifei
 * @date 2023/2/17 11:16
 */
public class PassportInterceptor implements HandlerInterceptor {
    @Autowired
    private PassGate<?> passGate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        passGate.verifyLogin(request, response, handler);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        UserContext.remove();
    }
}
