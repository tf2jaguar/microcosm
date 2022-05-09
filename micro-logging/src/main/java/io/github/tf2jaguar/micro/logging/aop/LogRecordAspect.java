package io.github.tf2jaguar.micro.logging.aop;

import io.github.tf2jaguar.micro.logging.annotation.LogRecord;
import io.github.tf2jaguar.micro.logging.filter.LogFilter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

/**
 * @author zhangguodong
 * @since 2022/5/5 16:21
 */
@Aspect
@Component
public class LogRecordAspect {
    private static final Logger log = LoggerFactory.getLogger(LogFilter.class);

    public static final String MDC_SESSION_ID = "session_id";

    @Pointcut(value = "@annotation(io.github.tf2jaguar.micro.logging.annotation.LogRecord)")
    public void getLogRecordPoint() {
    }

    @Around("getLogRecordPoint()")
    public Object asyncLog(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        LogRecord logRecordAnnotation = methodSignature.getMethod().getAnnotation(LogRecord.class);
        String methodName = methodSignature.getMethod().getName();
        Object[] args = proceedingJoinPoint.getArgs();

        if (logRecordAnnotation.setSessionIdBeforeRun() &&
                (logRecordAnnotation.requiredNewSessionId() || Objects.isNull(MDC.get(MDC_SESSION_ID)))) {
            MDC.put(MDC_SESSION_ID, UUID.randomUUID().toString());
        }

        if (log.isDebugEnabled()) {
            log.debug(String.format("run method %s args: %s", methodName, Arrays.toString(args)));
        }

        long startTime = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();
        long costTime = System.currentTimeMillis() - startTime;

        if (log.isDebugEnabled()) {
            log.debug(String.format("run method %s cost:%dms", methodName, costTime));
        }

        if (logRecordAnnotation.removeSessionIdAfterRun()) {
            MDC.remove(MDC_SESSION_ID);
        }
        return result;
    }
}
