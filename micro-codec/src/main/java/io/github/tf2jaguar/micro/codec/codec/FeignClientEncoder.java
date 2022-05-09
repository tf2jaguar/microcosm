package io.github.tf2jaguar.micro.codec.codec;

import io.github.tf2jaguar.micro.codec.util.ObjectMapperUtil;
import feign.RequestTemplate;
import feign.codec.EncodeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;

import java.lang.reflect.Type;

/**
 * @author zhangguodong
 */
public class FeignClientEncoder extends SpringEncoder {

    private static Logger log = LoggerFactory.getLogger(FeignClientEncoder.class);


    public FeignClientEncoder(ObjectFactory<HttpMessageConverters> messageConverters) {
        super(messageConverters);
    }

    @Override
    public void encode(Object object, Type bodyType, RequestTemplate template) throws EncodeException {

        try {
            super.encode(object, bodyType, template);
            template.body(ObjectMapperUtil.objectMapper.writeValueAsString(object));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}