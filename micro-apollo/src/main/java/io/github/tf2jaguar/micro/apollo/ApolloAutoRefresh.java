package io.github.tf2jaguar.micro.apollo;

import com.ctrip.framework.apollo.core.ConfigConsts;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author zhangguodong
 */
@Component
public class ApolloAutoRefresh implements ApplicationContextAware {
    private static final Logger log = LoggerFactory.getLogger(ApolloAutoRefresh.class);

    ApplicationContext applicationContext;

    @ApolloConfigChangeListener(value = ConfigConsts.NAMESPACE_APPLICATION)
    public void onChange(ConfigChangeEvent changeEvent) {
        for (String changedKey : changeEvent.changedKeys()) {
            log.info("apollo changed value:{}", changeEvent.getChange(changedKey));
        }
        refreshProperties(changeEvent);
    }

    /**
     * 更新相应的bean的属性值，主要是存在@ConfigurationProperties注解的bean
     */
    public void refreshProperties(ConfigChangeEvent changeEvent) {
        this.applicationContext.publishEvent(new EnvironmentChangeEvent(changeEvent.changedKeys()));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}