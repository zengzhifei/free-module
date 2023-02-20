package com.stoicfree.free.module.autoconfigure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.stoicfree.free.common.module.config.CommonProperties;
import com.stoicfree.free.common.module.support.LoggerLevel;
import com.stoicfree.free.common.module.support.msg.MailHelper;

/**
 * @author zengzhifei
 * @date 2023/2/3 17:14
 */
@Configuration
@EnableConfigurationProperties({CommonProperties.class})
public class CommonModuleAutoConfiguration {
    @Autowired
    private CommonProperties commonProperties;

    @Bean
    public LoggerLevel loggerLevel() {
        return new LoggerLevel(commonProperties.getLoggingLevels());
    }

    @Bean
    @ConditionalOnProperty(value = "free.common.mail")
    public MailHelper mailHelper() {
        return new MailHelper(commonProperties.getMail());
    }
}
