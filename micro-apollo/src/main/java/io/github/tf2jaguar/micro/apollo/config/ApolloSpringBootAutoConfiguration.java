package io.github.tf2jaguar.micro.apollo.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * microcosm apollo 模块入口
 *
 * @author ：zhangguodong
 * @since ：Created in 2021/4/25 下午4:50
 */
@Configuration
@ComponentScan("io.github.tf2jaguar.micro.apollo")
@EnableConfigurationProperties({ApolloConfiguration.class})
@ConditionalOnProperty(prefix = "micro.apollo", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ApolloSpringBootAutoConfiguration {

}
