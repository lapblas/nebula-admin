package com.nebula.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 日志拦截器配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "logging.interceptor")
public class LoggingInterceptorProperties {

    /**
     * 统一开关，false 时所有日志都不打印
     */
    private boolean enabled = true;

    /**
     * 基础信息开关（请求方式、URI、响应状态、耗时等）
     */
    private boolean basicInfo = true;

    /**
     * 客户端信息开关（IP、UserAgent、Session ID）
     */
    private boolean clientInfo = true;

    /**
     * 请求参数开关（URL 参数）
     */
    private boolean requestParams = true;

    /**
     * 请求体开关（Body）
     */
    private boolean requestBody = true;

    /**
     * 响应体开关
     */
    private boolean responseBody = true;

    /**
     * 请求头开关
     */
    private boolean requestHeaders = true;
}
