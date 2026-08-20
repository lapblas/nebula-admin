package com.nebula.system.vo.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 权限信息请求Vo
 */
@Data
public class PermissionReq {

    private Long id;

    /**
     * 权限名称
     */
    @NotBlank(message = "权限名称不能为空")
    @Size(min = 1, max = 50, message = "权限名称长度必须在1-50个字符之间")
    private String permissionName;

    /**
     * 权限标识
     */
    @NotBlank(message = "权限标识不能为空")
    @Size(min = 1, max = 100, message = "权限标识长度必须在1-100个字符之间")
    private String permissionKey;

    /**
     * 权限描述
     */
    @Size(max = 200, message = "权限描述长度不能超过200个字符")
    private String description;
}
