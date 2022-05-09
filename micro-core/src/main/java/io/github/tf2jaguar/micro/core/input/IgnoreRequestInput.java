package io.github.tf2jaguar.micro.core.input;

import java.lang.annotation.*;

/**
 * @author zhangguodong
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
public @interface IgnoreRequestInput {

}