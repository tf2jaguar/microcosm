package io.github.tf2jaguar.micro.qconf.ribbon;

import com.netflix.client.config.CommonClientConfigKey;
import com.netflix.client.config.DefaultClientConfigImpl;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.ConfigurationBasedServerList;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ServerList;
import net.qihoo.qconf.Qconf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.netflix.ribbon.PropertiesFactory;
import org.springframework.cloud.netflix.ribbon.RibbonClientName;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * ribbon配置：从qconf中获取服务器列表
 *
 * @author ：zhangguodong
 * @since 1.0.0
 */
public class RibbonConfig {
    /**
     * 连接超时
     */
    public static final int DEFAULT_CONNECT_TIMEOUT = 1000;
    /**
     * 读取超时
     */
    public static final int DEFAULT_READ_TIMEOUT = 2000;

    @RibbonClientName
    private String name = "client";

    /**
     * 客户端配置:连接超时，读取超时，客户端名称
     *
     * @return 客户端配置
     */
    @Bean
    public IClientConfig ribbonClientConfig() {
        DefaultClientConfigImpl config = new DefaultClientConfigImpl();
        config.loadProperties(this.name);
        config.set(CommonClientConfigKey.ConnectTimeout, DEFAULT_CONNECT_TIMEOUT);
        config.set(CommonClientConfigKey.ReadTimeout, DEFAULT_READ_TIMEOUT);
        config.set(CommonClientConfigKey.ServerListRefreshInterval, 1000);
        return config;
    }


    /**
     * 通过Qconf获取服务端地址
     *
     * @param environment       环境上下文对象
     * @param propertiesFactory 属性工厂类
     * @param config            客户端配置
     * @return 基于Qconf的服务发现地址，clientName.nameSpace.listOfServers
     */
    @Bean
    @ConditionalOnProperty(value = "micro.qconf.enabled", matchIfMissing = true)
    public ServerList<Server> ribbonServerList(Environment environment, PropertiesFactory propertiesFactory, IClientConfig config) {
        if (propertiesFactory.isSet(ServerList.class, name)) {
            return propertiesFactory.get(ServerList.class, config, name);
        }
        return new QconfBasedServerList(environment, name, config);
    }


}

/**
 * 通过Qconf获取服务器地址
 *
 * @author ：zhangguodong
 * @since 2.3.0
 */
class QconfBasedServerList extends ConfigurationBasedServerList {
    private static final Logger logger = LoggerFactory.getLogger(QconfBasedServerList.class);

    /**
     * qconf的命名空间
     */
    public static final String NAMESPACE = "qconf.url";

    private Environment environment;
    private String name;

    /**
     * 获取qconf key
     *
     * @return 从environment中获取的value
     */
    private String getQconfUrl() {
        return environment.getProperty(name + "." + NAMESPACE);
    }

    public QconfBasedServerList(Environment environment, String name, IClientConfig config) {
        this.environment = environment;
        this.name = name;
        super.initWithNiwsConfig(config);
    }

    @Override
    public List<Server> getInitialListOfServers() {
        return getServerList();
    }

    @Override
    public List<Server> getUpdatedListOfServers() {
        return getServerList();
    }

    /**
     * 调用Qconf native api，获取服务器列表
     *
     * @return 服务器列表
     */
    public List<Server> getServerList() {
        String qconfUrl = getQconfUrl();
        if (StringUtils.isEmpty(qconfUrl)) {
            return super.getUpdatedListOfServers();
        }
        List<String> allHost = null;
        try {
            allHost = Qconf.getAllHost(qconfUrl);
        } catch (Throwable e) {
            logger.error(qconfUrl + "\\t" + e.getMessage(), e);
        }

        if (CollectionUtils.isEmpty(allHost)) {
            logger.warn("qconf 通过 key:{} 找不到的 value，通过配置的 ribbon.listOfServers 获取地址", qconfUrl);
            return super.getUpdatedListOfServers();
        }
        String listOfServers = org.apache.commons.lang.StringUtils.join(allHost, ",");
        logger.debug("qconf 通过 key:{}(name:{}) 找到的 value:{}", qconfUrl, this.name, listOfServers);
        return derive(listOfServers);
    }
}