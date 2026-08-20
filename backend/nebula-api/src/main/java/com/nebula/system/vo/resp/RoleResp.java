package com.nebula.system.vo.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * 角色信息返回Vo
 */
@Data
public class RoleResp {
    /**
     * 角色ID
     */
    private Long id;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色标识
     */
    private String roleKey;

    /**
     * 角色描述
     */
    private String description;

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
     * 角色权限
     */
    private Set<PermissionResp> permissions;
}
