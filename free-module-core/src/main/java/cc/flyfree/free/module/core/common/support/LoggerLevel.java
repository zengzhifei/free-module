package cc.flyfree.free.module.core.common.support;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zengzhifei
 * @date 2022/8/28 19:04
 */
@Slf4j
public class LoggerLevel implements InitializingBean {
    private final String loggingLevels;

    @Autowired(required = false)
    private LoggingSystem loggingSystem;

    public LoggerLevel(String loggingLevels) {
        this.loggingLevels = loggingLevels;
    }

    @Override
    public void afterPropertiesSet() {
        this.setLoggingLevel(loggingLevels);
    }

    private void setLoggingLevel(String loggingLevels) {
        try {
            if (StringUtils.isNotBlank(loggingLevels)) {
                List<String> logLevels = Arrays.stream(loggingLevels.split(",")).collect(Collectors.toList());
                List<String> levels = Arrays.stream(LogLevel.values()).map(Enum::name).collect(Collectors.toList());
                for (String logLevel : logLevels) {
                    String[] configs = logLevel.split(":");
                    if (configs.length != 2) {
                        continue;
                    }
                    String logger = configs[0].trim();
                    if (StringUtils.isBlank(logger)) {
                        continue;
                    }
                    String levelStr = configs[1].trim().toUpperCase();
                    if (!levels.contains(levelStr)) {
                        continue;
                    }
                    LogLevel level = LogLevel.valueOf(levelStr);
                    loggingSystem.setLogLevel(configs[0], level);
                }
            }
        } catch (Exception e) {
            log.error("LoggerLevel setLevel fail", e);
        }
    }
}
