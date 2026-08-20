package com.nebula.advice;

import com.nebula.interceptor.LoggingInterceptor;
import com.nebula.utils.JsonUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.lang.reflect.Type;

/**
 * 请求体日志 Advice，用于记录请求体内容
 */
@RestControllerAdvice
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@Slf4j
public class RequestBodyLogAdvice extends RequestBodyAdviceAdapter {

    private final HttpServletRequest request;

    public RequestBodyLogAdvice(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType,
                          Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter,
                               Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 将请求体存储到request属性中
        String requestBody = JsonUtils.toJsonString(body);
        LoggingInterceptor.setRequestBody(request, requestBody);

        log.debug("请求体: {}", requestBody);
        return body;
    }

    @Override
    public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter,
                                 Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        LoggingInterceptor.setRequestBody(request, "");
        return body;
    }
}
