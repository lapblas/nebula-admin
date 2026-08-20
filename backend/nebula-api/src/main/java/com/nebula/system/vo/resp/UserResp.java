package com.nebula.system.vo.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * 用户信息返回Vo
 */
@Data
public class UserResp {
    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 是否是管理员
     */
    private Boolean isAdmin;

    /**
     * 用户类型：1-后台用户，2-普通用户
     */
    private Integer userType;

    /**
     * 用户角色
     */
    private Set<RoleResp> roles;
}