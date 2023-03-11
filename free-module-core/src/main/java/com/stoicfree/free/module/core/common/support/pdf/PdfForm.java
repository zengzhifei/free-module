package com.stoicfree.free.module.core.common.support.pdf;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zengzhifei
 * @date 2022/12/5 14:31
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PdfForm {
    /**
     * 表单类型
     */
    Type type() default Type.TEXT;

    /**
     * 字段前缀
     */
    String keyPrefix() default "";

    /**
     * 字段别名
     */
    String key() default "";

    enum Type {
        // 表单类型
        TEXT, IMAGE;
    }
}
