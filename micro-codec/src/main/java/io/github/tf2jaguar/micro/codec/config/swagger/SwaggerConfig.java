package io.github.tf2jaguar.micro.codec.config.swagger;

import io.swagger.annotations.Api;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author ：zhangguodong
 */
@Configuration
@EnableSwagger2
@ConditionalOnProperty(prefix = "swagger", name = "enabled", havingValue = "true")
public class SwaggerConfig {

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("开放接口")
                .description("Rest API接口")
                .version("1.0")
                .build();
    }

    /**
     * 封装swagger的请求体
     *
     * @return ParamsResolverAspect 切面
     */
    @ConditionalOnProperty(value = "request.body.params.enabled", havingValue = "true", matchIfMissing = true)
    @Bean
    public ParamsResolverAspect paramsResolverAspect() {
        return new ParamsResolverAspect();
    }

    /**
     * 封装swagger的响应体
     *
     * @return ReturnResolverAspect 切面
     */
    @ConditionalOnProperty(value = "response.body.result.enabled", havingValue = "true", matchIfMissing = true)
    @Bean
    public ReturnResolverAspect returnResolverAspect() {
        return new ReturnResolverAspect();
    }
}
