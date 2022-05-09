# microcosm

以功能分包，功能之间尽量减少相互依赖。每个功能作为最小单元供服务依赖，共同组成丰富的大宇宙。

## micro-apollo (封装apollo模块)

```
-- config
    |-- ApolloConfiguration 封装apollo模块配置类，从配置文件中读取是否开启封装apollo
    |-- ApolloSpringBootAutoConfiguration 封装apollo模块 springboot 自动配置类，封装apollo模块的入口
-- ApolloAutoRefresh 对 apollo 配置变更自动刷新
-- ApolloLoggerConfig 对 apollo 日志级别调整后自动刷新
```

## 模块依赖


## 使用建议

1. 在启动模块中添加 `micro-apollo` 模块的 pom 依赖
2. 实现了对 apollo 配置变更自动刷新
3. 实现了对 apollo 日志级别调整后自动刷新

