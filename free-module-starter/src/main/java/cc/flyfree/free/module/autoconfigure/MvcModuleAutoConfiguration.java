package cc.flyfree.free.module.autoconfigure;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.servlet.HandlerInterceptor;

import cc.flyfree.free.module.core.mvc.captcha.Captcha;
import cc.flyfree.free.module.core.mvc.captcha.anotation.advice.CreateCaptchaAdvice;
import cc.flyfree.free.module.core.mvc.captcha.anotation.advice.VerifyCaptchaAdvice;
import cc.flyfree.free.module.core.mvc.config.InterceptorWebMvcConfigure;
import cc.flyfree.free.module.core.mvc.config.MvcProperties;
import cc.flyfree.free.module.core.mvc.config.SwaggerProperties;
import cc.flyfree.free.module.core.mvc.filter.CrossDomainFilter;
import cc.flyfree.free.module.core.mvc.filter.RequestWrapperFilter;
import cc.flyfree.free.module.core.mvc.interceptor.LoggingInterceptor;
import cc.flyfree.free.module.core.mvc.interceptor.PassportInterceptor;
import cc.flyfree.free.module.core.mvc.passport.anotation.advice.LoginAdvice;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

/**
 * @author zengzhifei
 * @date 2023/2/3 17:14
 */
@Configuration
@ConditionalOnWebApplication
@ConditionalOnExpression("${free.mvc.enable:false}")
@EnableConfigurationProperties(MvcProperties.class)
public class MvcModuleAutoConfiguration {
    private final List<HandlerInterceptor> interceptors = new ArrayList<>();
    @Autowired
    private MvcProperties mvcProperties;

    @Bean
    public FilterRegistrationBean<RequestWrapperFilter> requestWrapperFilterBean() {
        FilterRegistrationBean<RequestWrapperFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new RequestWrapperFilter());
        bean.addUrlPatterns("/*");
        bean.setOrder(0);
        return bean;
    }

    @Bean
    public FilterRegistrationBean<CrossDomainFilter> crossDomainFilterBean() {
        FilterRegistrationBean<CrossDomainFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new CrossDomainFilter(mvcProperties.getCrossDomain()));
        bean.addUrlPatterns("/*");
        bean.setOrder(1);
        return bean;
    }

    @Bean
    public LoggingInterceptor loggingInterceptor() {
        LoggingInterceptor interceptor = new LoggingInterceptor(mvcProperties.getLogging());
        interceptors.add(interceptor);
        return interceptor;
    }

    @Bean
    public PassportInterceptor passportInterceptor() {
        PassportInterceptor interceptor = new PassportInterceptor();
        if (mvcProperties.getPassport().isEnable()) {
            interceptors.add(interceptor);
        }
        return interceptor;
    }

    @Bean
    @DependsOn({"loggingInterceptor", "passportInterceptor"})
    @ConditionalOnMissingBean
    public InterceptorWebMvcConfigure interceptorWebMvcConfigure() {
        return new InterceptorWebMvcConfigure(interceptors);
    }

    @Bean
    public LoginAdvice loginAdvice() {
        return new LoginAdvice();
    }

    @Bean
    public Captcha captcha() {
        return new Captcha(mvcProperties.getCaptcha());
    }

    @Bean
    public CreateCaptchaAdvice createCaptchaAdvice() {
        return new CreateCaptchaAdvice();
    }

    @Bean
    public VerifyCaptchaAdvice verifyCaptchaAdvice() {
        return new VerifyCaptchaAdvice();
    }

    @Bean
    @ConditionalOnExpression("${free.mvc.swagger.enable:false}")
    public OpenAPI swaggerOpenApi() {
        SwaggerProperties swaggerProperties = mvcProperties.getSwagger();
        Info info = new Info()
                .title(swaggerProperties.getTitle())
                .description(swaggerProperties.getDescription())
                .version(swaggerProperties.getVersion());
        OpenAPI openApi = new OpenAPI();
        openApi.setInfo(info);
        openApi.setOpenapi(swaggerProperties.getBasePackage());
        return openApi;
    }
}
