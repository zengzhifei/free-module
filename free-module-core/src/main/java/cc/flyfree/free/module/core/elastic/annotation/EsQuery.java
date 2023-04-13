package cc.flyfree.free.module.core.elastic.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zengzhifei
 * @date 2022/8/12 19:20
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EsQuery {
    /**
     * ES字段映射名
     *
     * @return
     */
    String name();

    /**
     * wildcard 匹配，占位符替换，如 *{}*
     *
     * @return
     */
    String wildcard() default "";

    /**
     * 语法
     *
     * @return
     */
    Clause clause() default Clause.MUST;

    enum Clause {
        /**
         * 语法类型
         */
        MUST, MUST_NOT, SHOULD, FILTER;
    }
}
