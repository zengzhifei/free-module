package com.stoicfree.free.module.autoconfigure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.stoicfree.free.module.core.redis.aop.advice.RepeatLimitAdvice;
import com.stoicfree.free.module.core.redis.config.RedisProperties;

/**
 * @author zengzhifei
 * @date 2023/2/3 17:14
 */
@Configuration
@ConditionalOnExpression("${free.redis.enable:false}")
@EnableConfigurationProperties({RedisProperties.class})
public class RedisModuleAutoConfiguration {
    @Autowired
    private RedisProperties redisProperties;

    @Bean
    public RepeatLimitAdvice repeatLimitAdvice() {
        return new RepeatLimitAdvice(redisProperties.getPrefix());
    }
}
