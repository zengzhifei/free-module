package com.stoicfree.free.common.module.config;

import java.util.Properties;

import org.springframework.boot.autoconfigure.freemarker.FreeMarkerProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import lombok.Data;

/**
 * @author zengzhifei
 * @date 2023/2/20 12:47
 */
@Data
public class MailProperties {
    /**
     * 邮箱host
     */
    private String host;
    /**
     * 邮箱端口
     */
    private Integer port;
    /**
     * 邮件来源名称
     */
    private String fromName;
    /**
     * 邮件地址
     */
    private String username;
    /**
     * 邮箱密码
     */
    private String password;
    private String protocol;
    /**
     * 邮箱编码
     */
    private String defaultEncoding;
    private Properties javaMailProperties;
    /**
     * 模板配置
     */
    @NestedConfigurationProperty
    private FreeMarkerProperties freeMarkerProperties;
}