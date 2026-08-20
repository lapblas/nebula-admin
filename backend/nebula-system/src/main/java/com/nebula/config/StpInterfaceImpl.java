package com.nebula.config;

import cn.dev33.satoken.stp.StpInterface;
import com.nebula.entity.User;
import com.nebula.service.UserService;
import com.nebula.system.enums.ClientType;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Sa-Token 权限认证实现类
 */
@Component
@AllArgsConstructor
public class StpInterfaceImpl implements StpInterface {

    private final UserService userService;

    /**
     * 返回指定用户拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        Long userId = Long.parseLong(loginId.toString());
        User user = userService.getUserEntityById(userId);

        // 仅管理端体系：超级管理员拥有所有权限
        if (ClientType.ADMIN.getCode().equals(loginType) && Boolean.TRUE.equals(user.getIsAdmin())) {
            List<String> allPermissions = new ArrayList<>();
            allPermissions.add("*");
            return allPermissions;
        }

        // 普通端与普通后台用户：按角色获取权限
        return user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(permission -> permission.getPermissionKey())
                .collect(Collectors.toList());
    }

    /**
     * 返回指定用户拥有的角色标识集合
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        Long userId = Long.parseLong(loginId.toString());
        User user = userService.getUserEntityById(userId);

        // 仅管理端体系：超级管理员拥有admin角色
        if (ClientType.ADMIN.getCode().equals(loginType) && Boolean.TRUE.equals(user.getIsAdmin())) {
            List<String> roles = new ArrayList<>();
            roles.add("admin");
            return roles;
        }

        // 普通端与普通后台用户：按角色获取角色标识
        return user.getRoles().stream()
                .map(role -> role.getRoleKey())
                .collect(Collectors.toList());
    }
}
