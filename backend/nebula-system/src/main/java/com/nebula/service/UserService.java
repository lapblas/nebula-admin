package com.nebula.service;

import com.nebula.entity.User;
import com.nebula.response.PageResult;
import com.nebula.system.vo.req.UserPageReq;
import com.nebula.system.vo.req.UserReq;
import com.nebula.system.vo.resp.UserResp;

import java.util.List;
import java.util.Set;

public interface UserService {
    UserResp saveUser(UserReq user);
    UserResp saveAdminUser(UserReq adminUser);
    UserResp getUserById(Long id);
    User getUserEntityById(Long id);
    List<UserResp> getAllUsers();
    PageResult<UserResp> pageUsers(UserPageReq req);
    UserResp updateUser(UserReq user);
    UserResp updateAdminUser(UserReq adminUser);
    void deleteUser(Long id);
    void deleteAdminUser(Long id);
    UserResp getUserByUsername(String username);
    User getUserEntityByUsername(String username);
    UserResp getUserByPhone(String phone);
    User getUserEntityByPhone(String phone);
    boolean existsByUsername(String username);
    boolean existsByPhone(String phone);

    // 角色管理
    void assignRoles(Long userId, Set<Long> roleIds);
    void removeRoles(Long userId, Set<Long> roleIds);
    Set<String> getUserRoles(Long userId);
    Set<String> getUserPermissions(Long userId);
}