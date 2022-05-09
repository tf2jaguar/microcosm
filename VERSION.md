- [最新版本](#%E6%9C%80%E6%96%B0%E7%89%88%E6%9C%AC)
- [更新日历](#%E6%9B%B4%E6%96%B0%E6%97%A5%E5%8E%86)
  - [1.0.0.RELEASE](#100release)
  - [1.0.1.RELEASE](#101release)
  - [1.1.0-SNAPSHOT](#110-snapshot)


# 最新版本

[1.0.1.RELEASE](#101release)

# 更新日历

## 1.1.0-SNAPSHOT

发布日期：`2021-07-01`

1. micro-core： 自定义服务异常枚举时实现 `ExceptionEnums` 如内置的常用异常状态枚举的实现方式 `public enum ErrorEnums implements ExceptionEnums`；封装了简单的分页请求入参和返回参数，封装了接口交互的出入参数格式类（通过 `micro-codec` 模块完成出入参数自动封装、解封装） 
2. micro-codec： 实现了对 `feign` 调用的出入参数驼峰转换；实现了对 `http` 调用出入参数驼峰转换，入参数 params 封装，出参数 `code、data、message` 封装；实现了对 `swagger` 展示中入参数、出参数封装
3. micro-except： 实现类拦截服务异常 `ServerException`，打印error日志，返回接口错误 ；拦截业务异常 `BusinessException`，不打印error日志，只返回接口错误 ；拦截参数绑定异常 `BindException`，打印error日志，返回接口错误 ；拦截方法参数异常 `MethodArgumentNotValidException`，打印error日志，返回接口错误 ；拦截全局异常 `Exception`，打印error日志，返回接口错误
4. 增加mybatis-plus代码生成器模块


## 1.0.1.RELEASE

发布日期：`2021-05-13`

1. 调整pom依赖和 [guideline](GUIDELINE.md)

## 1.0.0.RELEASE

发布日期：`2021-05-13`

1. micro-logging： 实现了统计经过 `http请求` 的出入参数记录，针对每个 `request` 的 `api日志` 用唯一的 `session_id` 进行区分；使用logback记录日志、记录 `all_log、error_log、api_log、access_log` 的日志并按照天做切分
2. micro-apollo： 实现了对 `apollo` 配置变更自动刷新；实现了对 `apollo` 日志级别调整后自动刷新
3. micro-qconf： 实现了从 `qconf` 中获取服务器列表，供给 `ribbon` 远程调用
3. micro-distribute： 利用 Twitter 的 Snowflake 算法实现分布式ID
