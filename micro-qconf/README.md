# microcosm

以功能分包，功能之间尽量减少相互依赖。每个功能作为最小单元供服务依赖，共同组成丰富的大宇宙。

## micro-qconf (封装qconf模块)

```
-- config
    |-- QconfConfiguration 封装qconf模块配置类，从配置文件中读取是否开启封装qconf模块
    |-- QconfSpringBootAutoConfiguration 封装qconf模块 springboot 自动配置类，封装qconf模块的入口
-- ribbon
    |-- GlobalRibbonConfig 处理 ribbon 全局配置
    |-- RibbonConfig 从 qconf 中获取服务器列表，供给 ribbon 远程调用
```

## 模块依赖


## 使用建议

1. 在进行 feign 调用的模块中添加 `micro-qconf` 模块的 pom 依赖
2. 实现了从 qconf 中获取服务器列表，供给 `ribbon` 远程调用
