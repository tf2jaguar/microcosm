package io.github.tf2jaguar.micro.codec.wapper;

import io.github.tf2jaguar.micro.core.output.IgnoreResponseResult;
import io.github.tf2jaguar.micro.core.output.OutputMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * 对controller中的返回 response 数据封装为统一的Result。在controller的方法或者类上 增加 @IgnoreResponseResult 时 不会生效。
 *
 * @author ：zhangguodong
 */
@RestControllerAdvice
public class ResponseResultBodyAdvice implements ResponseBodyAdvice<Object> {
    private static final Logger log = LoggerFactory.getLogger(ResponseResultBodyAdvice.class);
    private static final Class<? extends Annotation> IGNORE_ANNOTATION_TYPE = IgnoreResponseResult.class;

    @Value("${micro.codec.response.body.result.enabled:true}")
    private boolean resultEnable;

    @Value("${micro.codec.response.body.ignore:/swagger-resources,/v2/api-docs}")
    private List<String> ignoresUri;

    /**
     * 判断类或者方法是否使用了 @ResponseResultBody
     */
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return resultEnable && Jackson2HttpMessageConverter.class.isAssignableFrom(converterType);
    }

    /**
     * 当类或者方法使用了 @ResponseResultBody 就会调用这个方法
     */
    @Override
    public Object beforeBodyWrite(Object div, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        if (this.ignore(request.getURI().toString())) {
            return div;
        }// 添加忽略返回result的注解
        else if (!AnnotatedElementUtils.hasAnnotation(returnType.getContainingClass(), IGNORE_ANNOTATION_TYPE) &&
                !returnType.hasMethodAnnotation(IGNORE_ANNOTATION_TYPE)) {
            // 防止重复包裹的问题出现
            return div instanceof OutputMessage ? div : OutputMessage.success(div);
        } else {
            return div;
        }
    }

    /**
     * 判断url是否需要拦截
     */
    private boolean ignore(String uri) {
        for (String string : ignoresUri) {
            if (uri.contains(string)) {
                return true;
            }
        }
        return false;
    }
}