package com.stoicfree.free.module.core.common.config;

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
     * 开启邮箱功能
     */
    private boolean enable = false;
    /**
     * 邮箱协议,如qq邮箱:smtp
     */
    private String protocol;
    /**
     * 邮箱host,如qq邮箱:smtp.qq.com
     */
    private String host;
    /**
     * 邮箱端口,如qq邮箱:465
     */
    private Integer port;
    /**
     * 邮件来源名称,如:姓名
     */
    private String fromName;
    /**
     * 邮件地址,如:abc@qq.com
     */
    private String username;
    /**
     * 邮箱密码,qq邮箱密码为第三方授权码
     */
    private String password;
    /**
     * 邮箱编码
     */
    private String defaultEncoding = "UTF-8";
    /**
     * 邮箱额外配置,如qq邮箱:mail.smtp.ssl.enable: true
     */
    private Properties properties;
    /**
     * 模板配置,可不配
     */
    @NestedConfigurationProperty
    private FreeMarkerProperties freeMarkerProperties;
}