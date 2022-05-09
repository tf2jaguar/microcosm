package io.github.tf2jaguar.micro.apollo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author ：zhangguodong
 * @since ：Created in 2021/4/25 下午4:13
 */
@ConfigurationProperties(prefix = "micro.apollo")
public class ApolloConfiguration {
    private Boolean enabled = true;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
