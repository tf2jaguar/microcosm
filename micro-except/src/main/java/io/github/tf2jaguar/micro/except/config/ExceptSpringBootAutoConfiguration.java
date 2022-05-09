package io.github.tf2jaguar.micro.except.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * microcosm except 模块入口
 *
 * @author ：zhangguodong
 * @since ：Created in 2021/4/25 下午5:15
 */
@Configuration
@ComponentScan("io.github.tf2jaguar.micro.except")
@EnableConfigurationProperties({ExceptConfiguration.class})
@ConditionalOnProperty(prefix = "micro.except", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ExceptSpringBootAutoConfiguration {
}
