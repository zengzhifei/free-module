package cc.flyfree.free.module.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cc.flyfree.free.module.core.redis.aop.advice.RepeatLimitAdvice;
import cc.flyfree.free.module.core.redis.config.RedisProperties;

/**
 * @author zengzhifei
 * @date 2023/2/3 17:14
 */
@Configuration
@ConditionalOnExpression("${free.redis.enable:false}")
@EnableConfigurationProperties(RedisProperties.class)
public class RedisModuleAutoConfiguration {
    @Bean
    public RepeatLimitAdvice repeatLimitAdvice() {
        return new RepeatLimitAdvice();
    }
}
