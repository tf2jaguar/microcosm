package io.github.tf2jaguar.micro.distribute.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ：zhangguodong
 * @since ：Created in 2021/4/25 下午5:13
 */
@ConfigurationProperties(prefix = "micro.distribute")
public class DistributeConfiguration {

    private Boolean enabled = true;
    private List<String> machineList = new ArrayList<>();
    private Long dataCenterId;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<String> getMachineId() {
        return machineList;
    }

    public void setMachineId(List<String> machineId) {
        this.machineList = machineId;
    }

    public Long getDataCenterId() {
        return dataCenterId;
    }

    public void setDataCenterId(Long dataCenterId) {
        this.dataCenterId = dataCenterId;
    }
}
