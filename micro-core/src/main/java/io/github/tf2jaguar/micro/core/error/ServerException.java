package io.github.tf2jaguar.micro.core.error;

import org.springframework.lang.Nullable;

import java.text.MessageFormat;

/**
 * 服务异常
 *
 * @author zhangguodong
 */
public class ServerException extends RuntimeException {

    private static final long serialVersionUID = 8224074865591050440L;

    private Integer errno;

    public ServerException(Integer errno, @Nullable String errMsg) {
        super(errMsg);
        this.errno = errno;
    }

    public ServerException() {
        super(ErrorEnums.INTERNAL_EXCEPTION.message());
        this.errno = ErrorEnums.INTERNAL_EXCEPTION.code();
    }

    public ServerException(@Nullable ExceptionEnums errorEnum) {
        super(errorEnum != null ? errorEnum.message() : ErrorEnums.INTERNAL_EXCEPTION.message());
        this.errno = errorEnum != null ? errorEnum.code() : ErrorEnums.INTERNAL_EXCEPTION.code();
    }

    public ServerException(@Nullable ExceptionEnums errorEnum, String... message) {
        super((errorEnum != null ? MessageFormat.format(errorEnum.message(), message) : MessageFormat.format("{0}", message)));
        this.errno = errorEnum != null ? errorEnum.code() : ErrorEnums.INTERNAL_EXCEPTION.code();
    }

    public Integer getErrno() {
        return errno;
    }

    public void setErrno(Integer errno) {
        this.errno = errno;
    }

}