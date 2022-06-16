package io.github.tf2jaguar.micro.logging.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zhangguodong
 * @since 2022/5/5 16:20
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface LogRecord {

    boolean requiredNewSessionId() default false;

    boolean setSessionIdBeforeRun() default true;

    boolean removeSessionIdAfterRun() default true;

    boolean traceMethodRunTime() default false;
}
