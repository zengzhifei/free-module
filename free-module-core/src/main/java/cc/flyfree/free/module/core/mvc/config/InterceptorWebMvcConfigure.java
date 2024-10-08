package cc.flyfree.free.module.core.mvc.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import cc.flyfree.free.module.core.mvc.interceptor.LoggingInterceptor;
import cc.flyfree.free.module.core.mvc.interceptor.PassportInterceptor;

/**
 * @author zengzhifei
 * @date 2023/2/11 14:46
 */
public class InterceptorWebMvcConfigure implements WebMvcConfigurer {
    @Autowired
    private MvcProperties mvcProperties;

    private final List<HandlerInterceptor> interceptors;

    private static final List<String> SWAGGER_URLS = Arrays.asList("/swagger-ui.html", "/swagger-resources/**",
            "/webjars/**", "/error", "/csrf", "/");

    public InterceptorWebMvcConfigure(List<HandlerInterceptor> interceptors) {
        this.interceptors = interceptors == null ? new ArrayList<>(0) : interceptors;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/META-INF/resources/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        for (HandlerInterceptor interceptor : interceptors) {
            // 记录拦截器
            if (interceptor instanceof LoggingInterceptor) {
                addInterceptorAndSetProperties(registry, interceptor, mvcProperties.getLogging());
            }
            // 登录拦截器
            if (interceptor instanceof PassportInterceptor) {
                addInterceptorAndSetProperties(registry, interceptor, mvcProperties.getPassport());
            }
        }
    }

    private void addInterceptorAndSetProperties(InterceptorRegistry registry, HandlerInterceptor interceptor,
                                                BaseInterceptorProperties properties) {
        InterceptorRegistration registration = registry.addInterceptor(interceptor).order(properties.getOrder());
        if (StringUtils.isNotBlank(properties.getAddPaths())) {
            registration.addPathPatterns(Arrays.asList(properties.getAddPaths().split(",")));
        }
        registration.excludePathPatterns(SWAGGER_URLS);
        if (StringUtils.isNotBlank(properties.getExcludePaths())) {
            registration.excludePathPatterns(Arrays.asList(properties.getExcludePaths().split(",")));
        }
    }
}
