package io.github.tf2jaguar.micro.mybatisplus.config;

import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;

import java.util.Properties;

/**
 * @author 张豪
 * @since 2021/7/8 17:14
 */
public class MybatisPlusApplicationListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        Properties props = new Properties();
        props.setProperty("mybatis-plus.mapper-locations", "classpath*:/mapper/**/*.xml");
        props.setProperty("mybatis-plus.global-config.banner", "false");
        props.setProperty("mybatis-plus.global-config.db-config.id-type", "auto");
        props.setProperty("mybatis-plus.global-config.db-config.logic-delete-field", "isDeleted");
        props.setProperty("mybatis-plus.global-config.db-config.logic-delete-value", "1");
        props.setProperty("mybatis-plus.global-config.db-config.logic-not-delete-value", "0");
        ConfigurableEnvironment env = event.getEnvironment();
        env.getPropertySources()
                .addFirst(new PropertiesPropertySource("microMybatisPlus", props));
    }

}
