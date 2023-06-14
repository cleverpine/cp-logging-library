package com.cleverpine.springlogginglibrary.interceptors;

import com.cleverpine.springlogginglibrary.models.LoggingInfoContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.cleverpine.springlogginglibrary.util.Constants.SERVICE_ID;
import static com.cleverpine.springlogginglibrary.util.Constants.TRACE_ID;

@Component
public class LoggingPropertiesInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        var loggingContext = LoggingInfoContext.getInstance();
        Optional<String> traceId = Optional.ofNullable(request.getHeader(TRACE_ID));
        loggingContext.setTraceId(
                traceId.orElse(loggingContext.generateTraceId())
        );

        ThreadContext.put(SERVICE_ID, loggingContext.getServiceId());
        ThreadContext.put(TRACE_ID, loggingContext.getTraceId());
        loggingContext.getProperties().forEach(ThreadContext::put);

        return true;
    }
}
