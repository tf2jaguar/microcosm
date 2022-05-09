- [最新版本](#%E6%9C%80%E6%96%B0%E7%89%88%E6%9C%AC)
- [更新日历](#%E6%9B%B4%E6%96%B0%E6%97%A5%E5%8E%86)
  - [1.0.0-SNAPSHOT](#100-snapshot)


# 最新版本

[1.0.0-SNAPSHOT](#100-snapshot)

# 更新日历


## 1.0.0-SNAPSHOT

发布日期：`2021-05-13`

1. micro-logging： 实现了统计经过 `http请求` 的出入参数记录，针对每个 `request` 的 `api日志` 用唯一的 `session_id` 进行区分；使用logback记录日志、记录 `all_log、error_log、api_log、access_log` 的日志并按照天做切分
2. micro-apollo： 实现了对 `apollo` 配置变更自动刷新；实现了对 `apollo` 日志级别调整后自动刷新
3. micro-qconf： 实现了从 `qconf` 中获取服务器列表，供给 `ribbon` 远程调用
