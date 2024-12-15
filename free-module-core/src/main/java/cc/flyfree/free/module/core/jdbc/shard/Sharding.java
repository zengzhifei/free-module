package cc.flyfree.free.module.core.jdbc.shard;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zengzhifei
 * @date 2024/5/6 16:40
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Sharding {
    Strategy strategy();

    int mod() default 0;

    enum Strategy {
        // 策略类型
        MOD;
    }
}
