package com.nebula.system.vo.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 菜单信息返回Vo
 */
@Data
public class MenuResp {
    /**
     * 菜单ID
     */
    private Long id;

    /**
     * 父菜单ID
     */
    private Long parentId;

    /**
     * 菜单名称
     */
    private String menuName;

    /**
     * 路由地址
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 权限标识
     */
    private String permissionKey;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 菜单类型
     */
    private String menuType;

    /**
     * 是否可见
     */
    private Boolean isVisible;

    /**
     * 是否禁用
     */
    private Boolean isDisabled;

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
     * 子菜单
     */
    private List<MenuResp> children = new ArrayList<>();
}
