package io.github.tf2jaguar.micro.core.error;

/**
 * 异常状态枚举
 *
 * @author zhangguodong
 */
public enum ErrorEnums implements ExceptionEnums{

    /**
     * 系统异常
     */
    INTERNAL_EXCEPTION(10000, "系统异常"),
    /**
     * 参数错误，后可拼接具体 msg
     */
    ILLEGAL_ARGUMENT(10001, "参数错误: {0}-{1}"),
    /**
     * 业务错误
     */
    SERVICE_EXCEPTION(10002, "业务错误"),
    /**
     * 非法的数据格式，参数没有经过校验，后可拼接具体 msg
     */
    ILLEGAL_DATA(10003, "数据错误: {0}"),
    /**
     * 上传数据量太大
     */
    MULTIPART_TOO_LARGE(1004, "文件太大"),
    /**
     * 非法状态
     */
    ILLEGAL_STATE(10005, "非法状态 {0}"),
    /**
     * 缺少参数
     */
    MISSING_ARGUMENT(10006, "缺少参数"),
    /**
     * 非法访问
     */
    ILLEGAL_ACCESS(10007, "非法访问 {0}"),
    /**
     * 权限不足
     */
    UNAUTHORIZED(10008, "权限不足"),
    /**
     * 错误的请求
     */
    METHOD_NOT_ALLOWED(10009, "不支持的方法");

    private final int code;

    private final String message;


    ErrorEnums(int code, String message) {
        this.code = code;
        this.message = message;
    }


    /**
     * Return the integer value of this status code.
     */
    @Override
    public int code() {
        return this.code;
    }

    /**
     * Return the reason phrase of this status code.
     */
    @Override
    public String message() {
        return this.message;
    }

}