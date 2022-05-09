<div align="center">  
    <p>
        <a href="https://tf2jaguar.github.io"><img src="https://badgen.net/badge/tf2jaguar/read?icon=sourcegraph&color=4ab8a1" alt="read" /></a>
        <img src="https://badgen.net/github/stars/tf2jaguar/microcosm?icon=github&color=4ab8a1" alt="stars" />
        <img src="https://badgen.net/github/forks/tf2jaguar/microcosm?icon=github&color=4ab8a1" alt="forks" />
        <img src="https://badgen.net/github/open-issues/tf2jaguar/microcosm?icon=github" alt="issues" />
    </p>
</div>

# microcosm

Try to reduce mutual dependence between functions and functions. Each function is used as the minimum unit for service dependencies.

以功能分包，功能之间尽量减少相互依赖，每个功能作为最小单元供服务依赖。

## 使用教程

参考: [guideline](GUIDELINE.md)

## 发版日历

最新发版及调整参考: [version](VERSION.md)


## micro-logging

### 使用建议

1. 在启动模块中添加 `micro-logging` 模块的 pom 依赖
2. 针对 form表单提交、文件上传/下载等接口，请通过spring-boot配置 `micro.logging.api.ignore=` 忽略日志打印
3. 实现了统计经过 `http请求` 的出入参数记录，针对每个 `request` 在日志中有唯一的 `session_id` 进行区分
4. 使用logback记录日志、记录 all_log、error_log、api_log、access_log 日志并按照天做切分

## micro-apollo

### 使用建议

1. 在启动模块中添加 `micro-apollo` 模块的 pom 依赖
2. 实现了对 `apollo` 配置变更自动刷新
3. 实现了对 `apollo` 日志级别调整后自动刷新

## micro-qconf

### 使用建议

1. 在进行 `feign` 调用的模块中添加 `micro-qconf` 模块的 pom 依赖
2. 实现了从 `qconf` 中获取服务器列表，供给 `ribbon` 远程调用

## micro-distribute

### 使用建议

1. 在启动模块中添加 `micro-distribute` 模块的 pom 依赖
2. 服务集群部署，请通过 `micro.distribute.machine-list=` 指定当前机器集群（单机时无需配置，默认1），用来计算分布式id生成的机器id；
3. 服务部署在多个数据中心，请通过 `micro.distribute.data-center-id=` 指定当前数据中心id编号（单机时无需配置，默认1），用来计算分布式id生成的数据中心id

## micro-core

### 使用建议

1. 在底层接口模块中添加 `micro-core` 模块的 pom 依赖
2. 自定义服务异常枚举时实现 `ExceptionEnums` 如内置的常用异常状态枚举的实现方式 `public enum ErrorEnums implements ExceptionEnums`
3. 封装了简单的分页请求入参和返回参数
4. 封装了接口交互的出入参数格式类（通过 `micro-codec` 模块完成出入参数自动封装、解封装）

## micro-codec

### 使用建议

1. 在启动模块中添加 `micro-codec` 模块的 pom 依赖
2. 实现了对 `feign` 调用的出入参数驼峰转换
3. 实现了对 `http` 调用出入参数驼峰转换，入参数 `params` 封装，出参数 `code、data、message` 封装
4. 实现了对 `swagger` 展示中入参数、出参数封装

## micro-except

### 使用建议

1. 在启动模块中添加 `micro-except` 模块的 pom 依赖
2. 拦截服务异常 `ServerException` 打印error日志，返回接口错误
3. 拦截业务异常 `BusinessException` 不打印error日志，只返回接口错误
4. 拦截参数绑定异常 `BindException` 打印error日志，返回接口错误
5. 拦截方法参数异常 `MethodArgumentNotValidException` 打印error日志，返回接口错误
6. 拦截全局异常 `Exception` 打印error日志，返回接口错误
