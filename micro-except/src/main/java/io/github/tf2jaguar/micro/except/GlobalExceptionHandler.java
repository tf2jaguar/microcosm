package io.github.tf2jaguar.micro.except;

import io.github.tf2jaguar.micro.core.error.BusinessException;
import io.github.tf2jaguar.micro.core.error.ServerException;
import io.github.tf2jaguar.micro.core.output.OutputMessage;
import io.micrometer.core.instrument.Metrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 全局异常拦截
 *
 * @author zhangguodong
 */
@SuppressWarnings("rawtypes")
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static final int ERROR_CODE = 1;


    /**
     * 拦截进入controller 参数绑定异常
     *
     * @param request request请求
     * @param e       参数绑定异常
     * @return spring http response entity
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(BindException.class)
    public OutputMessage bindExceptionHandler(HttpServletRequest request, BindException e) {
        return filterErrorMsg(request, e.getBindingResult().getFieldErrors(), e);
    }

    /**
     * 拦截 方法参数异常异常
     *
     * @param request request请求
     * @param e       业务异常
     * @return spring http response entity
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public OutputMessage methodArgumentNotValidExceptionHandler(HttpServletRequest request, MethodArgumentNotValidException e) {
        return filterErrorMsg(request, e.getBindingResult().getFieldErrors(), e);
    }

    /**
     * 拦截服务异常
     *
     * @param request request请求
     * @param e       业务异常
     * @return spring http response entity
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(ServerException.class)
    public OutputMessage serverExceptionHandler(HttpServletRequest request, ServerException e) {
        String uri = request.getRequestURI();
        log.error("Capture a server exception. uri: {}, msg: {}", uri, e.getMessage(), e);
        Integer code = e.getErrno();

        perfMonitor(uri, ERROR_CODE, e.getClass().getName());
        return OutputMessage.error(code, e.getMessage());
    }

    /**
     * 拦截业务异常，不打印error日志，只返回接口错误
     *
     * @param request request请求
     * @param e       业务异常
     * @return spring http response entity
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(BusinessException.class)
    public OutputMessage businessExceptionHandler(HttpServletRequest request, BusinessException e) {
        String uri = request.getRequestURI();
        log.warn("Capture a business exception. uri: {}, msg: {}", uri, e.getMessage(), e);
        Integer code = e.getErrno();

        perfMonitor(uri, ERROR_CODE, e.getClass().getName());
        return OutputMessage.error(code, e.getMessage());
    }

    /**
     * 拦截服务异常
     *
     * @param request request请求
     * @param e       业务异常
     * @return spring http response entity
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(value = Exception.class)
    public OutputMessage exceptionHandler(HttpServletRequest request, Exception e) {
        String uri = request.getRequestURI();
        log.error("Capture a global exception. uri: {}, msg: {}", uri, e.getMessage(), e);
        String msg = (e.getMessage() == null || e.getMessage().length() > 80) ? "操作失败,请稍后重试" : e.getMessage();

        perfMonitor(uri, ERROR_CODE, e.getClass().getName());
        return OutputMessage.error(ERROR_CODE, msg);
    }

    private OutputMessage filterErrorMsg(HttpServletRequest request, List<FieldError> fieldErrors, Exception e) {
        String uri = request.getRequestURI();
        log.error("Capture a data check exception. uri: {}, msg: {}", uri, e.getMessage(), e);

        List<String> errorMsgList = new ArrayList<>();
        for (FieldError fieldError : fieldErrors) {
            errorMsgList.add(fieldError.getField() + " " + fieldError.getDefaultMessage());
        }
        String msg = String.join(";", errorMsgList);

        perfMonitor(uri, ERROR_CODE, e.getClass().getName());
        return OutputMessage.error(ERROR_CODE, msg);
    }

    private void perfMonitor(String uri, Integer code, String exc) {
        Metrics.counter("api.exception", "uri", uri, "code", Integer.toString(code), "exception", exc)
                .increment();
    }
}