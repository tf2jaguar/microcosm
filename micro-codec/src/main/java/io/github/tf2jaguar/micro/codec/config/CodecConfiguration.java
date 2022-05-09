package io.github.tf2jaguar.micro.codec.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author ：zhangguodong
 * @since ：Created in 2021/4/25 下午5:13
 */
@ConfigurationProperties(prefix = "micro.codec")
public class CodecConfiguration {
    private Boolean enabled = true;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
