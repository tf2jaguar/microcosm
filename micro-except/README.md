# microcosm

以功能分包，功能之间尽量减少相互依赖。每个功能作为最小单元供服务依赖，共同组成丰富的大宇宙。

## micro-except (异常处理模块)

```
-- config
    |-- ExceptConfiguration 异常处理模块配置类，从配置文件中读取是否开启异常全局处理
    |-- ExceptSpringBootAutoConfiguration 异常处理模块 springboot 自动配置类，异常处理模块的入口
-- GlobalExceptionHandler 全局异常捕获器，由 `@ControllerAdvice` 实现
```

## 模块依赖

- micro-core，模块中定义了框架基本的异常类、出入参数封装类


## 使用建议

1. 在启动模块中添加 `micro-except` 模块的 pom 依赖
2. 拦截服务异常 `ServerException` ，打印error日志，返回接口错误
3. 拦截业务异常 `BusinessException` ，不打印error日志，只返回接口错误
4. 拦截参数绑定异常 `BindException` ，打印error日志，返回接口错误
5. 拦截方法参数异常 `MethodArgumentNotValidException` ，打印error日志，返回接口错误
6. 拦截全局异常 `Exception` ，打印error日志，返回接口错误
