package com.nebula.system.vo.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 权限信息返回Vo
 */
@Data
public class PermissionResp {
    /**
     * 权限ID
     */
    private Long id;

    /**
     * 权限名称
     */
    private String permissionName;

    /**
     * 权限标识
     */
    private String permissionKey;

    /**
     * 权限描述
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
}
