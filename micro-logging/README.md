# microcosm

以功能分包，功能之间尽量减少相互依赖。每个功能作为最小单元供服务依赖，共同组成丰富的大宇宙。

## micro-logging (日志统计模块)

```
-- annotation
    |-- LogRecord 方法级别日志注解
-- aop
    |-- LogRecordAspect 配合 LogRecord 注解，在日志中打印进入方法、退出方法的记录，同时统计方法的执行时间
-- config
    |-- LogConfiguration 日志统计模块配置类，从配置文件中读取是否开启日志统计模块
    |-- LogSpringBootAutoConfiguration 日志统计模块 springboot 自动配置类，日志统计模块的入口
-- filter
    |-- LogFilter 继承自 OncePerRequestFilter，通过重复读请求体，并在日志中加入唯一的 `session_id` 进行单次请求的链路进行记录。
-- util
    |-- IPUtil 统计请求 IP 记录
-- wapper
    |-- RequestWrapper http请求装饰器
    |-- ResponseWrapper http响应装饰器
```

## 模块依赖


## 使用建议

1. 在启动模块中添加 `micro-logging` 模块的 pom 依赖
2. 针对 form表单提交、文件上传/下载等接口，请通过spring-boot配置 `micro.logging.api.ignore=` 忽略日志打印
3. 实现了统计经过 `http请求` 的出入参数记录，针对每个 `request` 在日志中有唯一的 `session_id` 进行区分
4. 使用logback记录日志、记录 all_log、error_log、api_log、access_log 日志并按照天做切分
