package com.stoicfree.free.module.core.mvc.passport.anotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

/**
 * @author zengzhifei
 * @date 2023/2/18 11:28
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Auth {
    @AliasFor("roles")
    String[] value() default "";

    @AliasFor("value")
    String[] roles() default "";

    String[] excludeRoles() default "";
}
