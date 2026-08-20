package com.nebula.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 管理员初始化配置
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "admin.init")
public class AdminInitConfig {

    /**
     * 是否启用管理员初始化
     */
    private boolean enable = false;

    /**
     * 管理员用户名
     */
    private String username = "admin";

    /**
     * 管理员密码
     */
    private String password = "admin123";

    /**
     * 管理员手机号
     */
    private String phone = "13800000000";
}