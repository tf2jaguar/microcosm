# microcosm (小宇宙)

以功能分包，功能之间尽量减少相互依赖。每个功能作为最小单元供服务依赖，共同组成丰富的大宇宙。

## micro-codec (出入参封装模块)

```
-- config
    |-- CodecConfiguration 出入参封装模块配置类，从配置文件中读取是否开启出入参封装
    |-- CodecSpringBootAutoConfiguration 出入参封装模块 springboot 自动配置类，出入参封装模块的入口
    |-- swagger
          |-- ParamsResolverAspect 对swagger的入参进行封装
          |-- ReturnResolverAspect 对swagger的出参进行封装
          |-- SwaggerConfig 对swagger配置类，从配置文件中读取是否开启swagger
-- codec
    |-- FeignClientDecoder 对feign调用时的解码器
    |-- FeignClientEncoder 对feign调用时的编码器
-- util
    |-- ObjectMapperUtil 使用 `com.fasterxml.jackson.databind.ObjectMapper` 操作下划线转驼峰的相互转换
-- wapper
    |-- Jackson2HttpMessageConverter 继承自 `MappingJackson2HttpMessageConverter` 对入参数进行解析
    |-- ResponseResultBodyAdvice 使用 `@RestControllerAdvice` 实现

```

## 模块依赖

- micro-core，模块中定义了框架基本的异常类、出入参数封装类

## 使用建议

1. 在启动模块中添加 `micro-codec` 模块的 pom 依赖
2. 实现了对 feign 调用的出入参数驼峰转换
3. 实现了对 http 调用出入参数驼峰转换，入参数 params 封装，出参数 code、data、message 封装
4. 实现了对 swagger 展示中入参数、出参数封装
