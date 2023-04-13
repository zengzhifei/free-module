package cc.flyfree.free.module.core.jdbc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * @author zengzhifei
 * @date 2023/2/12 16:57
 */
@Data
@ConfigurationProperties(prefix = "free.jdbc")
public class JdbcProperties {
    /**
     * 是否开启db
     */
    private boolean enable = false;
    /**
     * 实体类包路径
     */
    private String entityPackage;
}
