package com.nebula.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token 配置类
 */
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    /**
     * 注册 Sa-Token 拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 管理端接口：校验 admin 账号体系
        registry.addInterceptor(new SaInterceptor(handle -> StpAdminUtil.checkLogin()))
                .addPathPatterns("/user/**", "/role/**", "/permission/**", "/menu/**");

        // 用户端接口：校验 user 账号体系（按实际用户端路径配置）
        registry.addInterceptor(new SaInterceptor(handle -> StpUserUtil.checkLogin()))
                .addPathPatterns("/app/**");
    }

}
