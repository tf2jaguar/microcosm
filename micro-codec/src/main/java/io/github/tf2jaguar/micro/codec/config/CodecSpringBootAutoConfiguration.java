package io.github.tf2jaguar.micro.codec.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.tf2jaguar.micro.codec.util.ObjectMapperUtil;
import io.github.tf2jaguar.micro.codec.wapper.Jackson2HttpMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

/**
 * @author zhangguodong
 */
@Configuration
@ComponentScan("io.github.tf2jaguar.micro.codec")
@EnableConfigurationProperties({CodecConfiguration.class})
@ConditionalOnProperty(prefix = "micro.codec", name = "enabled", havingValue = "true", matchIfMissing = true)
public class CodecSpringBootAutoConfiguration {

    @Value("${micro.codec.request.body.params.enabled:true}")
    private boolean paramsEnable;

    /**
     * 注册message-converter
     * 自动转换驼峰为snake形式
     *
     * @return HttpMessageConverters 自定义json解析
     */
    @Bean
    public HttpMessageConverters jackson() {
        ObjectMapper objectMapper = ObjectMapperUtil.objectMapper;
        MappingJackson2HttpMessageConverter converter = new Jackson2HttpMessageConverter(paramsEnable, objectMapper);
        return new HttpMessageConverters(converter);
    }

}