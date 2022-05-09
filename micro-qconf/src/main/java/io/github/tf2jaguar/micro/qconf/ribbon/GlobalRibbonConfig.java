package io.github.tf2jaguar.micro.qconf.ribbon;

import org.springframework.cloud.netflix.ribbon.RibbonClients;

/**
 * @author ：zhangguodong
 */
@RibbonClients(defaultConfiguration = RibbonConfig.class)
public class GlobalRibbonConfig {

}