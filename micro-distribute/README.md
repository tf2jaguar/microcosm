# microcosm

以功能分包，功能之间尽量减少相互依赖。每个功能作为最小单元供服务依赖，共同组成丰富的大宇宙。

## micro-distribute (封装分布式模块)

```
-- config
    |-- DistributeConfiguration 分布式模块配置类，从配置文件中读取是否开启分布式模块；通过 `micro.distribute.machine-list=` 指定当前机器集群（单机时无需配置，默认1） ，用来计算机器id；
        通过 `micro.distribute.data-center-id=` 指定当前数据中心id编号（单机时无需配置，默认1） 
    |-- DistributeSpringBootAutoConfiguration 分布式模块 springboot 自动配置类，分布式模块的入口
-- id
    |-- IdWorker 分布式ID生成器
    |-- SnowFlake Twitter 的 Snowflake
-- util
    |-- HostUtil 获取当前机器名称、机器IP
```

## 模块依赖


## 使用建议

1. 在启动模块中添加 `micro-distribute` 模块的 pom 依赖
2. 服务集群部署，请通过 `micro.distribute.machine-list=` 指定当前机器集群（单机时无需配置，默认1），用来计算分布式id生成的机器id；
3. 服务部署在多个数据中心，请通过 `micro.distribute.data-center-id=` 指定当前数据中心id编号（单机时无需配置，默认1），用来计算分布式id生成的数据中心id

