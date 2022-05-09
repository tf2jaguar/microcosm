package io.github.tf2jaguar.micro.core.output;

import java.lang.annotation.*;

/**
 * @author zhangguodong
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
public @interface IgnoreResponseResult {

}