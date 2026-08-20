package com.nebula.system.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

/**
 * 客户端类型（端注册表）
 *
 * 每新增一个端，只需在此枚举增加一行：
 *  - code：Sa-Token 的 loginType，登录体系隔离标识（同时作为 token 值前缀，如 admin_xxxx）
 *  - allowedUserTypes：该端允许登录的身份类型（对应 sys_user.user_type）
 *    1-后台用户，2-普通用户
 * 各端共用同一请求头 Authorization，token 值内嵌 loginType 前缀用于区分所属端。
 */
@Getter
@AllArgsConstructor
public enum ClientType {

    /**
     * 管理后台
     */
    ADMIN("admin", "管理后台", Set.of(1)),

    /**
     * 用户端（H5/App/小程序等普通用户端共用）
     */
    USER("user", "用户端", Set.of(2));

    /**
     * Sa-Token loginType，同时作为 token 值前缀
     */
    private final String code;

    /**
     * 端描述
     */
    private final String description;

    /**
     * 允许登录的身份类型集合（sys_user.user_type）
     */
    private final Set<Integer> allowedUserTypes;

    /**
     * 从 token 值解析所属端（token 格式：{code}_{随机串}，如 admin_xxxx / user_xxxx）
     * @param token token 值
     * @return 匹配的端，未匹配返回 null
     */
    public static ClientType parseFromToken(String token) {
        if (token != null) {
            for (ClientType type : values()) {
                if (token.startsWith(type.getCode() + "_")) {
                    return type;
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return code;
    }
}
