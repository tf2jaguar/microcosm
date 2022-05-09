package io.github.tf2jaguar.micro.codec.codec;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import io.github.tf2jaguar.micro.codec.util.ObjectMapperUtil;
import feign.FeignException;
import feign.Response;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Objects;

/**
 * @author zhangguodong
 */
public class FeignClientDecoder extends SpringDecoder {

    private static Logger log = LoggerFactory.getLogger(FeignClientDecoder.class);

    public FeignClientDecoder(ObjectFactory<HttpMessageConverters> messageConverters) {
        super(messageConverters);
    }

    @Override
    public Object decode(Response response, Type type) throws IOException, FeignException {
        String bodyStr = IOUtils.toString(response.body().asInputStream());
        log.debug("begin decode type:{} resp:{}", type, bodyStr);
        ObjectMapper objectMapper = ObjectMapperUtil.objectMapper;

        if (type instanceof ParameterizedType && !StringUtils.isEmpty(bodyStr)) {
            JavaType javaType = this.getJavaType(type);
            if (Objects.nonNull(javaType)) {
                bodyStr = new ObjectMapper().writeValueAsString(objectMapper.readValue(bodyStr, javaType));
            }
        }

        Response build = this.rebuildResponse(response, bodyStr);
        return super.decode(build, type);
    }

    private Response rebuildResponse(Response response, String bodyStr) {
        log.debug("rebuild response. body:{}", bodyStr);
        return Response.builder()
                .body(bodyStr, Charset.defaultCharset())
                .headers(response.headers())
                .reason(response.reason())
                .status(response.status())
                .request(response.request())
                .build();
    }

    /**
     * 根据泛型获取要转换的 javaType
     * 只支持 Axx<B> 一个泛型的 type
     *
     * @param type 泛型type
     * @return JavaType
     */
    private JavaType getJavaType(Type type) {
        ParameterizedType generic = (ParameterizedType) type;
        Type actualClass = generic.getActualTypeArguments()[0];
        JavaType javaType = null;
        try {
            javaType = TypeFactory.defaultInstance().constructParametricType(Class.forName(generic.getRawType().getTypeName()),
                    TypeFactory.defaultInstance().constructType(actualClass));
        } catch (ClassNotFoundException e) {
            log.error("decode generic response error. {}", type);
            e.printStackTrace();
        }
        return javaType;
    }

}