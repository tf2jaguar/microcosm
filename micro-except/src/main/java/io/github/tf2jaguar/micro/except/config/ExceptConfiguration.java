package io.github.tf2jaguar.micro.except.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author ：zhangguodong
 * @since ：Created in 2021/4/25 下午5:13
 */
@ConfigurationProperties(prefix = "micro.except")
public class ExceptConfiguration {
    private Boolean enabled = true;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
