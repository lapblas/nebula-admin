package com.nebula.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "sys_role")
public class Role implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("id")
    private Long id;

    @Column(name = "role_name", nullable = false, unique = true, length = 50)
    @Comment("角色名称")
    private String roleName;

    @Column(name = "role_key", nullable = false, unique = true, length = 50)
    @Comment("角色标识")
    private String roleKey;

    @Column(name = "description", length = 200)
    @Comment("角色描述")
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

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "sys_role_permission",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions = new HashSet<>();

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
