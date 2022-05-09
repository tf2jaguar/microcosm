package io.github.tf2jaguar.micro.mybatisplus.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author 张豪
 * @since 2021/6/30 10:33
 */
@ConfigurationProperties(prefix = "micro.mybatis-plus")
public class MybatisPlusProperties {

    private Boolean enabled;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
