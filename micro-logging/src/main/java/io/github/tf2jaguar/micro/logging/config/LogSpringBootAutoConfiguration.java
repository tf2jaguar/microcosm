package io.github.tf2jaguar.micro.logging.config;

import io.github.tf2jaguar.micro.logging.filter.LogFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * microcosm logging 模块入口
 *
 * @author ：zhangguodong
 * @since ：Created in 2021/4/25 下午5:13
 */
@Configuration
@ComponentScan("io.github.tf2jaguar.micro.logging")
@EnableConfigurationProperties({LogConfiguration.class})
@ConditionalOnProperty(prefix = "micro.logging", name = "enabled", havingValue = "true", matchIfMissing = true)
public class LogSpringBootAutoConfiguration {

    @Value("${micro.logging.api.ignore:/*/actuator/health}")
    private List<String> ignoresUri;

    @Bean
    public FilterRegistrationBean logFilterRegistrationBean() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new LogFilter(ignoresUri));
        registration.addUrlPatterns("/*");
        registration.setName("logFilter");
        registration.setOrder(1);
        return registration;
    }
}
