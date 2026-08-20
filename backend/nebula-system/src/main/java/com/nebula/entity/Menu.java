package com.nebula.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "sys_menu")
public class Menu implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("id")
    private Long id;

    @Column(name = "parent_id", nullable = false)
    @Comment("父菜单ID，0表示顶级菜单")
    private Long parentId = 0L;

    @Column(name = "menu_name", nullable = false, length = 50)
    @Comment("菜单名称")
    private String menuName;

    @Column(name = "path", length = 200)
    @Comment("路由地址")
    private String path;

    @Column(name = "component", length = 200)
    @Comment("组件路径")
    private String component;

    @Column(name = "icon", length = 100)
    @Comment("菜单图标")
    private String icon;

    @Column(name = "permission_key", length = 100)
    @Comment("权限标识，关联sys_permission表")
    private String permissionKey;

    @Column(name = "sort_order", nullable = false)
    @Comment("排序")
    private Integer sortOrder = 0;

    @Column(name = "menu_type", nullable = false, length = 20)
    @Comment("菜单类型：DIR-目录，MENU-菜单，BUTTON-按钮")
    private String menuType;

    @Column(name = "is_visible", nullable = false)
    @Comment("是否可见：0-隐藏，1-显示")
    private Boolean isVisible = true;

    @Column(name = "is_disabled", nullable = false)
    @Comment("是否禁用：0-启用，1-禁用")
    private Boolean isDisabled = false;

    @Column(name = "create_time", nullable = false)
    @Comment("创建时间")
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false)
    @Comment("更新时间")
    private LocalDateTime updateTime;

    @Column(name = "is_deleted", nullable = false)
    @Comment("是否已删除：0-否，1-是")
    private Boolean isDeleted = false;

    @Transient
    private List<Menu> children = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createTime = now;
        updateTime = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}
