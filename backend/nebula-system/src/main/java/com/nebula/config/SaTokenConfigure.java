package com.nebula.config;

import cn.dev33.satoken.strategy.SaStrategy;
import cn.dev33.satoken.util.SaFoxUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

/**
 * Sa-Token 自定义 Token 生成策略
 *
 * 在 token 值中嵌入 loginType 前缀（如 admin_xxxx / user_xxxx），
 * 使 token 自描述所属端：header 合并后（统一 Authorization），
 * 后端可通过解析 token 前缀直接定位登录体系，无需逐个探测。
 */
@Configuration
public class SaTokenConfigure {

    @PostConstruct
    public void rewriteSaStrategy() {
        SaStrategy.instance.createToken = (loginId, loginType) ->
                loginType + "_" + SaFoxUtil.getRandomString(32);
    }
}
