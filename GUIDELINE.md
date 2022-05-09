- [讲在前边](#%E8%AE%B2%E5%9C%A8%E5%89%8D%E8%BE%B9)
- [使用](#%E4%BD%BF%E7%94%A8)
  - [父版本添加版本控制](#%E7%88%B6%E7%89%88%E6%9C%AC%E6%B7%BB%E5%8A%A0%E7%89%88%E6%9C%AC%E6%8E%A7%E5%88%B6)
  - [api模块增加依赖](#api%E6%A8%A1%E5%9D%97%E5%A2%9E%E5%8A%A0%E4%BE%9D%E8%B5%96)
    - [配置模块依赖](#%E9%85%8D%E7%BD%AE%E6%A8%A1%E5%9D%97%E4%BE%9D%E8%B5%96)
    - [自定义服务异常枚举](#%E8%87%AA%E5%AE%9A%E4%B9%89%E6%9C%8D%E5%8A%A1%E5%BC%82%E5%B8%B8%E6%9E%9A%E4%B8%BE)
  - [common模块增加依赖](#common%E6%A8%A1%E5%9D%97%E5%A2%9E%E5%8A%A0%E4%BE%9D%E8%B5%96)
    - [配置模块依赖](#%E9%85%8D%E7%BD%AE%E6%A8%A1%E5%9D%97%E4%BE%9D%E8%B5%96-1)
  - [main模块添加依赖](#main%E6%A8%A1%E5%9D%97%E6%B7%BB%E5%8A%A0%E4%BE%9D%E8%B5%96)
    - [配置模块依赖](#%E9%85%8D%E7%BD%AE%E6%A8%A1%E5%9D%97%E4%BE%9D%E8%B5%96-2)

# 讲在前边

1. 这个项目只是对 `spring-boot` 类的项目的一些增强，如果你的项目不是用 `spring-boot` 开发的，那么它对你的用处可能没有那么大
2. 本指导用例依据个人开发习惯编写而成，项目结构参考另一个脚手架项目 [dust](https://github.com/tf2jaguar/dust.git)
3. *无论在什么情况下，我都会倾听你的意见并对项目做出改进*

# 使用

## 父版本添加版本控制

```xml
<properties>
    <last-vision>最新版本</last-vision>
</properties>

<dependencyManagement>
    <dependencies>
      <!-- microcosm start -->
      <dependency>
        <groupId>io.github.tf2jaguar.micro</groupId>
        <artifactId>micro-logging</artifactId>
        <version>${revision}</version>
      </dependency>

      <dependency>
        <groupId>io.github.tf2jaguar.micro</groupId>
        <artifactId>micro-apollo</artifactId>
        <version>${revision}</version>
      </dependency>

      <dependency>
        <groupId>io.github.tf2jaguar.micro</groupId>
        <artifactId>micro-qconf</artifactId>
        <version>${revision}</version>
      </dependency>

      <dependency>
        <groupId>io.github.tf2jaguar.micro</groupId>
        <artifactId>micro-distribute</artifactId>
        <version>${revision}</version>
      </dependency>
      
      <dependency>
        <groupId>io.github.tf2jaguar.micro</groupId>
        <artifactId>micro-core</artifactId>
        <version>${revision}</version>
      </dependency>

      <dependency>
        <groupId>io.github.tf2jaguar.micro</groupId>
        <artifactId>micro-codec</artifactId>
        <version>${revision}</version>
      </dependency>

      <dependency>
        <groupId>io.github.tf2jaguar.micro</groupId>
        <artifactId>micro-except</artifactId>
        <version>${revision}</version>
      </dependency>
      
      <dependency>
        <groupId>io.github.tf2jaguar.micro</groupId>
        <artifactId>micro-micro-mybatis-plus</artifactId>
        <version>${revision}</version>
      </dependency>
      <!-- microcosm end -->
    </dependencies>
</dependencyManagement>
```

## api模块增加依赖

这里主要增加一些核心类库，包括但不限于出入参数封装 [InputMessage](micro-core/src/main/java/io/github/micro/core/input/InputMessage.java),
[OutputMessage](micro-core/src/main/java/io/github/micro/core/output/OutputMessage.java),
服务异常 [ServerException](micro-core/src/main/java/io/github/micro/core/error/ServerException.java),
业务异常 [BusinessException](micro-core/src/main/java/io/github/micro/core/error/BusinessException.java)，
核心异常枚举 [ErrorEnums](micro-core/src/main/java/io/github/micro/core/error/ErrorEnums.java)

### 配置模块依赖

```xml

<dependencies>
    <dependency>
        <groupId>io.github.tf2jaguar.micro</groupId>
        <artifactId>micro-core</artifactId>
    </dependency>
</dependencies>
```

### 自定义服务异常枚举

你可以通过实现 `ExceptionEnums` 接口，来定义自己的服务异常枚举，用来适配服务异常、业务异常。两种异常在统一异常捕获模块，会产生不一样的效果哦

```java
public enum GmmpErrorCodes implements ExceptionEnums {
    // 异常
    INTERNAL_EXCEPT(10000, "系统内部异常"),
    INVALID_SIGN(10001, "请求加密协议错误"),
    INVALID_IP(10002, "请求IP地址异常"),
    INVALID_DATA(10003, "数据错误"),
    INVALID_PARAMS(10004, "参数错误"),
    INVALID_CITY_ID(10005, "无效的城市编号"),
    INVALID_ENTERPRISE_ID(10006, "无效的企业编号"),
    // …… 省略部分
    ;
    private int code;
    private String message;
    public static final int MODULE = 200;

    GmmpErrorCodes(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int code() {
        return this.code;
    }

    @Override
    public String message() {
        return this.message;
    }
}
```

## common模块增加依赖

这个模块主要是通用能力的封装，包括但不限于调用第三方服务、通用工具类处理

此处增加 出入参数封装依赖（qconf适配的依赖）

### 配置模块依赖

```xml

<dependencies>
    <dependency>
        <groupId>io.github.tf2jaguar.micro</groupId>
        <artifactId>micro-codec</artifactId>
    </dependency>
</dependencies>
```

如果你的服务使用 `qconf` 而不是 `nacos` 进行服务发现，那么你还需要添加以下依赖

```xml

<dependencies>
    <dependency>
        <groupId>io.github.tf2jaguar.micro</groupId>
        <artifactId>micro-qconf</artifactId>
    </dependency>
</dependencies>
```

## main模块添加依赖

这个模块是整个服务的启动模块，是 `springboot` 项目的启动类所在的模块

这里添加日志处理、apollo配置中心、统一异常捕获的依赖

### 配置模块依赖

```xml
<dependencies>
    <dependency>
        <groupId>io.github.tf2jaguar.micro</groupId>
        <artifactId>micro-logging</artifactId>
    </dependency>

    <dependency>
        <groupId>io.github.tf2jaguar.micro</groupId>
        <artifactId>micro-apollo</artifactId>
    </dependency>

    <dependency>
        <groupId>io.github.tf2jaguar.micro</groupId>
        <artifactId>micro-except</artifactId>
    </dependency>
</dependencies>
```