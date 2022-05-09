package io.github.tf2jaguar.micro.core.error;

/**
 * 异常接口, 各业务异常枚举需要继承此类
 *
 * @author zhangguodong
 */
public interface ExceptionEnums {

    /**
     * 错误码
     */
    int code();

    /**
     * 错误信息
     */
    String message();
}