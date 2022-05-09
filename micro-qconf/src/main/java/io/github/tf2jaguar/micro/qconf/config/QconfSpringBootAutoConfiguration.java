package io.github.tf2jaguar.micro.qconf.config;

import com.netflix.ribbon.Ribbon;
import io.github.tf2jaguar.micro.qconf.ribbon.GlobalRibbonConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * microcosm qconf 模块入口
 *
 * @author ：zhangguodong
 * @since ：Created in 2021/4/25 下午4:50
 */
@Configuration
@ConditionalOnClass(Ribbon.class)
@Import({GlobalRibbonConfig.class})
@ComponentScan("io.github.tf2jaguar.micro.qconf")
@EnableConfigurationProperties({QconfConfiguration.class})
@ConditionalOnProperty(prefix = "micro.qconf", name = "enabled", havingValue = "true", matchIfMissing = true)
public class QconfSpringBootAutoConfiguration {

}
