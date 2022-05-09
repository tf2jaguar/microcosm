package io.github.tf2jaguar.micro.codec.wapper;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import io.github.tf2jaguar.micro.core.input.IgnoreRequestInput;
import io.github.tf2jaguar.micro.core.input.InputMessage;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author ：zhangguodong
 */
public class Jackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {

    private static final Class<? extends Annotation> IGNORE_ANNOTATION_TYPE = IgnoreRequestInput.class;

    private boolean paramsEnable;

    public Jackson2HttpMessageConverter(boolean paramsEnable, ObjectMapper objectMapper) {
        super(objectMapper);
        this.paramsEnable = paramsEnable;
    }

    @Override
    public Object read(Type type, Class<?> aClass, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        // response 和不做特殊处理
        if (inputMessage instanceof ClientHttpResponse) {
            return super.read(type, aClass, inputMessage);
        }
        // 已经包装的
        if ((type instanceof ParameterizedType) && ((ParameterizedType) type).getRawType() == InputMessage.class) {
            return super.read(type, aClass, inputMessage);
        }

        // 需要跳过
        if (aClass.isAnnotationPresent(IGNORE_ANNOTATION_TYPE)) {
            return super.read(type, aClass, inputMessage);
        }

        if (paramsEnable) {
            JavaType javaType = TypeFactory.defaultInstance().constructParametricType(InputMessage.class,
                    TypeFactory.defaultInstance().constructType(type));

            InputMessage inputMessageObj = getObjectMapper().readValue(inputMessage.getBody(), javaType);

            if (inputMessageObj == null || inputMessageObj.getParams() == null) {
                throw new RuntimeException("params not exist!");
            }

            return inputMessageObj.getParams();
        }

        return super.read(type, aClass, inputMessage);
    }
}
