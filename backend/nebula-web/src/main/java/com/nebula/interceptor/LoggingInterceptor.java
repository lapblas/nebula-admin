package com.nebula.interceptor;

import com.nebula.config.LoggingInterceptorProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Enumeration;

/**
 * 日志拦截器，用于记录请求日志和统计请求时间
 */
@Component
@Slf4j
@AllArgsConstructor
public class LoggingInterceptor implements HandlerInterceptor {

    private static final String START_TIME_ATTR = "logging.startTime";
    private static final String REQUEST_BODY_ATTR = "logging.requestBody";
    private static final String RESPONSE_BODY_ATTR = "logging.responseBody";

    private final LoggingInterceptorProperties properties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!properties.isEnabled()) {
            return true;
        }
        // 使用request.setAttribute存储开始时间
        request.setAttribute(START_TIME_ATTR, System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if (!properties.isEnabled()) {
            return;
        }

        // 从request.getAttribute获取开始时间
        Long startTime = (Long) request.getAttribute(START_TIME_ATTR);
        if (startTime == null) {
            return;
        }

        long endTime = System.currentTimeMillis();
        long executeTime = endTime - startTime;

        // 构建日志信息
        StringBuilder logBuilder = new StringBuilder();
        logBuilder.append("\n========== 接口请求日志 ==========\n");

        // 基础信息
        if (properties.isBasicInfo()) {
            String method = request.getMethod();
            String uri = request.getRequestURI();
            String queryString = request.getQueryString();
            int status = response.getStatus();
            String contentType = response.getContentType();

            logBuilder.append("【基础信息】\n");
            logBuilder.append("  请求方式: ").append(method).append("\n");
            logBuilder.append("  请求URI: ").append(uri).append("\n");
            logBuilder.append("  完整URL: ").append(request.getRequestURL()).append(queryString != null ? "?" + queryString : "").append("\n");
            logBuilder.append("  响应状态: ").append(status).append("\n");
            logBuilder.append("  响应类型: ").append(contentType != null ? contentType : "无").append("\n");
            logBuilder.append("  请求耗时: ").append(executeTime).append("ms\n");
        }

        // 客户端信息
        if (properties.isClientInfo()) {
            String remoteAddr = request.getRemoteAddr();
            String userAgent = request.getHeader("User-Agent");
            String referer = request.getHeader("Referer");
            String sessionId = request.getRequestedSessionId();

            logBuilder.append("【客户端信息】\n");
            logBuilder.append("  IP: ").append(remoteAddr).append("\n");
            logBuilder.append("  UserAgent: ").append(userAgent != null ? userAgent : "无").append("\n");
            logBuilder.append("  Referer: ").append(referer != null ? referer : "无").append("\n");
            logBuilder.append("  Session ID: ").append(sessionId != null ? sessionId : "无").append("\n");
        }

        // 请求参数（URL 参数）
        if (properties.isRequestParams()) {
            logBuilder.append("【请求参数（URL）】\n");
            Enumeration<String> paramNames = request.getParameterNames();
            boolean hasParams = false;
            while (paramNames.hasMoreElements()) {
                hasParams = true;
                String paramName = paramNames.nextElement();
                String[] paramValues = request.getParameterValues(paramName);
                logBuilder.append("  ").append(paramName).append(": ");
                if (paramValues.length == 1) {
                    logBuilder.append(paramValues[0]);
                } else {
                    logBuilder.append("[");
                    for (int i = 0; i < paramValues.length; i++) {
                        if (i > 0) logBuilder.append(", ");
                        logBuilder.append(paramValues[i]);
                    }
                    logBuilder.append("]");
                }
                logBuilder.append("\n");
            }
            if (!hasParams) {
                logBuilder.append("  无 URL 参数\n");
            }
        }

        // 请求体（Body）- 从request.getAttribute获取
        if (properties.isRequestBody()) {
            String requestBody = (String) request.getAttribute(REQUEST_BODY_ATTR);
            logBuilder.append("【请求体（Body）】\n");
            if (requestBody != null && !requestBody.isEmpty()) {
                logBuilder.append("  ").append(requestBody).append("\n");
            } else {
                logBuilder.append("  无请求体\n");
            }
        }

        // 响应体 - 从request.getAttribute获取
        if (properties.isResponseBody()) {
            String responseBody = (String) request.getAttribute(RESPONSE_BODY_ATTR);
            logBuilder.append("【响应体】\n");
            if (responseBody != null && !responseBody.isEmpty()) {
                logBuilder.append("  ").append(responseBody).append("\n");
            } else {
                logBuilder.append("  无响应体\n");
            }
        }

        // 请求头（只显示重要的）
        if (properties.isRequestHeaders()) {
            logBuilder.append("【请求头】\n");
            Enumeration<String> headerNames = request.getHeaderNames();
            boolean hasHeaders = false;
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                if (isImportantHeader(headerName)) {
                    hasHeaders = true;
                    logBuilder.append("  ").append(headerName).append(": ").append(request.getHeader(headerName)).append("\n");
                }
            }
            if (!hasHeaders) {
                logBuilder.append("  无重要请求头\n");
            }
        }

        // 异常信息
        if (ex != null) {
            logBuilder.append("【异常信息】\n");
            logBuilder.append("  ").append(ex.getMessage()).append("\n");
            log.error(logBuilder.toString(), ex);
        } else {
            log.info(logBuilder.toString());
        }

        // 清理属性
        request.removeAttribute(START_TIME_ATTR);
        request.removeAttribute(REQUEST_BODY_ATTR);
        request.removeAttribute(RESPONSE_BODY_ATTR);
    }

    /**
     * 判断是否是重要的请求头
     */
    private boolean isImportantHeader(String headerName) {
        String lowerHeaderName = headerName.toLowerCase();
        return lowerHeaderName.equals("authorization") ||
               lowerHeaderName.equals("content-type") ||
               lowerHeaderName.equals("user-agent") ||
               lowerHeaderName.equals("referer") ||
               lowerHeaderName.equals("x-requested-with");
    }

    /**
     * 设置请求体到request属性
     */
    public static void setRequestBody(HttpServletRequest request, String requestBody) {
        request.setAttribute(REQUEST_BODY_ATTR, requestBody);
    }

    /**
     * 设置响应体到request属性
     */
    public static void setResponseBody(HttpServletRequest request, String responseBody) {
        request.setAttribute(RESPONSE_BODY_ATTR, responseBody);
    }
}
