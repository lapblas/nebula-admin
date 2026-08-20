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
@Table(name = "sys_user")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("id")
    private Long id;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    @Comment("用户名")
    private String username;

    @Column(name = "password", nullable = false, length = 255)
    @Comment("密码")
    private String password;

    @Column(name = "phone", nullable = false, unique = true, length = 20)
    @Comment("手机号")
    private String phone;

    @Column(name = "create_time", nullable = false)
    @Comment("创建时间")
    private LocalDateTime  createTime;

    @Column(name = "update_time", nullable = false)
    @Comment("更新时间")
    private LocalDateTime  updateTime;

    @Column(name = "is_admin", nullable = false)
    @Comment("是否是管理员：0-否，1-是")
    private Boolean isAdmin = false;

    @Column(name = "user_type")
    @Comment("用户类型：1-后台用户，2-普通用户")
    private Integer userType = 1;

    @Column(name = "is_deleted", nullable = false)
    @Comment("是否已删除：0-否，1-是")
    private Boolean isDeleted = false;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "sys_user_role",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

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