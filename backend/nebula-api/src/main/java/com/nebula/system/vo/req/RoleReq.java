package com.nebula.system.vo.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 角色信息请求Vo
 */
@Data
public class RoleReq {

    private Long id;

    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空")
    @Size(min = 1, max = 50, message = "角色名称长度必须在1-50个字符之间")
    private String roleName;

    /**
     * 角色标识
     */
    @NotBlank(message = "角色标识不能为空")
    @Size(min = 1, max = 50, message = "角色标识长度必须在1-50个字符之间")
    private String roleKey;

    /**
     * 角色描述
     */
    @Size(max = 200, message = "角色描述长度不能超过200个字符")
    private String description;
}
