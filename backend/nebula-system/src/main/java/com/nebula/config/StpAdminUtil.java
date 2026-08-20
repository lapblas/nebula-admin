package com.nebula.config;

import cn.dev33.satoken.stp.StpLogic;
import org.springframework.stereotype.Component;

/**
 * 管理端账号体系工具类（loginType = "admin"）
 * 与原生 StpUtil（login）体系完全隔离，用于管理端/用户端 token 隔离
 * 注意：必须由 Spring 管理（@Component），确保服务启动时即加载本类、
 * 完成 StpLogic 注册，否则注解鉴权会报"未能获取对应StpLogic"
 */
@Component
public class StpAdminUtil {

    /** 账号体系标识 */
    public static final String TYPE = "admin";

    /** 底层 StpLogic 对象，构造时自动注册到 SaManager */
    public static final StpLogic stpLogic = new StpLogic(TYPE);

    /** 登录 */
    public static void login(Object loginId) {
        stpLogic.login(loginId);
    }

    /** 获取当前会话 token */
    public static String getTokenValue() {
        return stpLogic.getTokenValue();
    }

    /** 当前会话是否已登录 */
    public static boolean isLogin() {
        return stpLogic.isLogin();
    }

    /** 校验是否登录，未登录则抛出异常 */
    public static void checkLogin() {
        stpLogic.checkLogin();
    }

    /** 获取当前登录用户 id */
    public static long getLoginIdAsLong() {
        return stpLogic.getLoginIdAsLong();
    }

    /** 登出 */
    public static void logout() {
        stpLogic.logout();
    }
}
