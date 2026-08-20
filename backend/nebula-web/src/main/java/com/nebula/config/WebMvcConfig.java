package com.nebula.config;

import com.nebula.interceptor.LoggingInterceptor;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置类，用于注册拦截器
 */
@Configuration
@AllArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final LoggingInterceptor loggingInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册日志拦截器，拦截所有请求
        registry.addInterceptor(loggingInterceptor)
                .addPathPatterns("/**")
                // 排除静态资源
                .excludePathPatterns("/static/**", "/css/**", "/js/**", "/images/**", "/favicon.ico");
    }
}
