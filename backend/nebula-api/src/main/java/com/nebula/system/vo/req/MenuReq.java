package com.nebula.system.vo.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 菜单信息请求Vo
 */
@Data
public class MenuReq {

    private Long id;

    /**
     * 父菜单ID
     */
    @NotNull(message = "父菜单ID不能为空")
    private Long parentId = 0L;

    /**
     * 菜单名称
     */
    @NotBlank(message = "菜单名称不能为空")
    @Size(min = 1, max = 50, message = "菜单名称长度必须在1-50个字符之间")
    private String menuName;

    /**
     * 路由地址
     */
    @Size(max = 200, message = "路由地址长度不能超过200个字符")
    private String path;

    /**
     * 组件路径
     */
    @Size(max = 200, message = "组件路径长度不能超过200个字符")
    private String component;

    /**
     * 菜单图标
     */
    @Size(max = 100, message = "菜单图标长度不能超过100个字符")
    private String icon;

    /**
     * 权限标识
     */
    @Size(max = 100, message = "权限标识长度不能超过100个字符")
    private String permissionKey;

    /**
     * 排序
     */
    private Integer sortOrder = 0;

    /**
     * 菜单类型：DIR-目录，MENU-菜单，BUTTON-按钮
     */
    @NotBlank(message = "菜单类型不能为空")
    private String menuType;

    /**
     * 是否可见
     */
    private Boolean isVisible = true;

    /**
     * 是否禁用
     */
    private Boolean isDisabled = false;
}
