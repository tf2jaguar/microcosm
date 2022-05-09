package io.github.tf2jaguar.micro.distribute.config;

import io.github.tf2jaguar.micro.distribute.id.IdWorker;
import io.github.tf2jaguar.micro.distribute.util.HostUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Objects;

/**
 * microcosm distribute 模块入口
 *
 * @author ：zhangguodong
 * @since ：Created in 2021/4/25 下午5:18
 */
@Configuration
@ComponentScan("io.github.tf2jaguar.micro.distribute")
@EnableConfigurationProperties({DistributeConfiguration.class})
@ConditionalOnProperty(prefix = "micro.distribute", name = "enabled", havingValue = "true", matchIfMissing = true)
public class DistributeSpringBootAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(DistributeSpringBootAutoConfiguration.class);

    @Autowired
    private DistributeConfiguration distributeConfiguration;

    @Bean
    public IdWorker idWorker() {
        return new IdWorker(calculateMachineId(), calculateDataCenterId());
    }

    private long calculateDataCenterId() {
        long curDataCenterId = 1L;
        if (Objects.nonNull(distributeConfiguration.getDataCenterId())) {
            curDataCenterId = distributeConfiguration.getDataCenterId();
        }

        if (log.isDebugEnabled()) {
            log.debug("id-worker use data-center-id: {}", curDataCenterId);
        }
        return curDataCenterId;
    }

    private long calculateMachineId() {
        List<String> configMachineIds = distributeConfiguration.getMachineId();
        String curHostAddress = HostUtil.getHostAddress();
        long curMachineId = 1L;

        for (int i = 0; i < configMachineIds.size(); i++) {
            if (curHostAddress.equals(configMachineIds.get(i))) {
                curMachineId = (long) (i + 1);
                break;
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("id-worker use machine-id: {} cur-host-address: {}", curMachineId, curHostAddress);
        }
        return curMachineId;
    }
}
