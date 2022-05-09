package io.github.tf2jaguar.micro.core.output;

import io.github.tf2jaguar.micro.core.error.ExceptionEnums;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ：zhangguodong
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OutputMessage<T> {

    public static final OutputMessage<Object> DEFAULT = OutputMessage.success(null);
    private Integer code;
    private String message;
    private T data;

    public static <T> OutputMessage<T> success(T data) {
        return new OutputMessage<T>(0, "成功！", data);
    }

    public static <T> OutputMessage<T> error(ExceptionEnums exceptionEnums) {
        return new OutputMessage<T>(exceptionEnums.code(), exceptionEnums.message(), null);
    }

    public static <T> OutputMessage<T> error(Integer errorCode, String errorMessage) {
        return new OutputMessage<T>(errorCode, errorMessage, null);
    }

}
