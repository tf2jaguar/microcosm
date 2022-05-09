package io.github.tf2jaguar.micro.apollo;

import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 自动刷新日志
 *
 * @author zhangguodong
 */
@Component
public class ApolloLoggerConfig {
    private static final Logger log = LoggerFactory.getLogger(ApolloLoggerConfig.class);
    private static final String LOGGER_TAG = "logging.level.";

    @Resource
    private LoggingSystem loggingSystem;

    @ApolloConfigChangeListener
    private void configChangeListener(ConfigChangeEvent changeEvent) {
        if (loggingSystem == null) {
            log.warn("loggingSystem 无法自动注入!");
            return;
        }
        for (String changedKey : changeEvent.changedKeys()) {
            if (!changedKey.toLowerCase().startsWith(LOGGER_TAG)) {
                continue;
            }
            String oldValue = changeEvent.getChange(changedKey).getOldValue();
            String newValue = changeEvent.getChange(changedKey).getNewValue();
            if (newValue == null) {
                continue;
            }
            LogLevel level = LogLevel.valueOf(newValue.toUpperCase());
            loggingSystem.setLogLevel(changedKey.replace(LOGGER_TAG, ""), level);
            log.info("logging level has changed. changedKey:{}, oldValue:{}, newValue:{}", changedKey, oldValue, newValue);
        }
    }
}