package io.github.tf2jaguar.micro.logging.filter;

import com.alibaba.fastjson.JSON;
import io.github.tf2jaguar.micro.logging.wapper.RequestWrapper;
import io.github.tf2jaguar.micro.logging.wapper.ResponseWrapper;
import io.github.tf2jaguar.micro.logging.util.IPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * @author ：zhangguodong
 * @since ：Created in 2021/7/1 14:45
 */
public class LogFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(LogFilter.class);
    private final ThreadLocal<Long> startTime = new ThreadLocal<>();
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final List<String> ignoresUri;

    public LogFilter(List<String> ignoresUri) {
        this.ignoresUri = ignoresUri;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (this.ignore(request.getRequestURI())) {
            if (log.isDebugEnabled()) {
                log.debug("micro logging has ignored uri: '{} {}'", request.getMethod(), request.getRequestURI());
            }
            filterChain.doFilter(request, response);
            return;
        }

        this.recordMark();
        Map<String, String[]> parameterMap = request.getParameterMap();
        RequestWrapper requestWrapper = new RequestWrapper((HttpServletRequest) request);
        log.info("REQUEST:'{} {}'; IP:{}; HEADER:{}; ARGS:{}", request.getMethod(), request.getRequestURI(),
                IPUtil.getIp(request), this.getRequestHeaderStr(request), requestWrapper.getBodyString());
        ResponseWrapper responseWrapper = new ResponseWrapper(response);
        filterChain.doFilter(requestWrapper, responseWrapper);

        String result = responseWrapper.getResponseData();
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(result.getBytes());
        outputStream.flush();
        outputStream.close();

        long costTime = System.currentTimeMillis() - this.startTime.get();
        log.info("RESPONSE:{}; COST_TIME:{}ms", result, costTime);
        this.clearMark();
    }


    private String getRequestHeaderStr(HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();
        Map<String, String> headerMap = new HashMap<>();
        while (headerNames.hasMoreElements()) {
            String headerKey = headerNames.nextElement();
            headerMap.put(headerKey, request.getHeader(headerKey));
        }
        return JSON.toJSONString(headerMap);
    }

    private void recordMark() {
        startTime.set(System.currentTimeMillis());
        MDC.put("session_id", UUID.randomUUID().toString());
    }

    private void clearMark() {
        startTime.remove();
        MDC.remove("session_id");
    }

    /**
     * 判断url是否需要拦截
     */
    private boolean ignore(String uri) {
        for (String u : ignoresUri) {
            if (pathMatcher.match(u, uri)) {
                return true;
            }
        }
        return false;
    }
}
