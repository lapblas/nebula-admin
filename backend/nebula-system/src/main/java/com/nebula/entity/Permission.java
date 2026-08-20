package com.nebula.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "sys_permission")
public class Permission implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("id")
    private Long id;

    @Column(name = "permission_name", nullable = false, length = 50)
    @Comment("权限名称")
    private String permissionName;

    @Column(name = "permission_key", nullable = false, unique = true, length = 100)
    @Comment("权限标识")
    private String permissionKey;

    @Column(name = "description", length = 200)
    @Comment("权限描述")
    private String description;

    @Column(name = "create_time", nullable = false)
    @Comment("创建时间")
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false)
    @Comment("更新时间")
    private LocalDateTime updateTime;

    @Column(name = "is_deleted", nullable = false)
    @Comment("是否已删除：0-否，1-是")
    private Boolean isDeleted = false;

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
