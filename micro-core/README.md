# microcosm

以功能分包，功能之间尽量减少相互依赖。每个功能作为最小单元供服务依赖，共同组成丰富的大宇宙。

## micro-core (核心类库模块)

```
-- bo
    |-- PageRequest 分页请求类
    |-- PageResponse 分页响应类
-- error
    |-- BusinessException 业务异常类，该类在 `micro-except` 模块中仍会返回错误code和msg，但是不会打印error级别异常日志
    |-- ServerException 服务异常类，该类 `micro-except` 模块中会返回错误code和msg，同时打印error级别异常日志
    |-- ExceptionEnums  异常接口, 各业务异常枚举需要继承此类，可与 `BusinessException`、`ServerException`、`Verify` 等工具配合使用
    |-- ErrorEnums 常用的异常状态枚举，支持参数动态拼接异常参数
    |-- Verify 校验工具类，可以方便抛出异常
-- input
    |-- IgnoreRequestInput 是否忽略通用入参的包装
    |-- InputMessage 通用入参包装类
-- output
    |-- IgnoreResponseResult 是否忽略通用返回类的包装
    |-- OutputMessage 通用出参包装类

```

## 模块依赖


## 使用建议

1. 在底层接口模块中添加 `micro-core` 模块的 pom 依赖
2. 自定义服务异常枚举时实现 `ExceptionEnums` 如内置的常用异常状态枚举的实现方式 `public enum ErrorEnums implements ExceptionEnums` 
3. 封装了简单的分页请求入参和返回参数
4. 封装了接口交互的出入参数格式类（通过 `micro-codec` 模块完成出入参数自动封装、解封装）
