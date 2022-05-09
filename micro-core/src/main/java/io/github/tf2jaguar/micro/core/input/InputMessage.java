package io.github.tf2jaguar.micro.core.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhangguodong
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InputMessage<T> {
    public T params;

    public static <T> InputMessage<T> params(T params) {
        return new InputMessage<>(params);
    }

}
