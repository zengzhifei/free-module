package cc.flyfree.free.module.core.redis.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * @author zengzhifei
 * @date 2023/2/14 14:45
 */
@Data
@ConfigurationProperties("free.redis")
public class RedisProperties {
    private boolean enable = false;
    private boolean log = false;
}
