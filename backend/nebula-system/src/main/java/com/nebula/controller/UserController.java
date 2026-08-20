package com.nebula.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.nebula.response.PageResult;
import com.nebula.response.Response;
import com.nebula.service.UserService;
import com.nebula.system.vo.req.UserPageReq;
import com.nebula.system.vo.req.UserReq;
import com.nebula.system.vo.resp.UserResp;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 创建用户
     * @param user 用户信息
     * @return 创建成功的用户信息
     */
    @PostMapping("/add")
    public Response<UserResp> createUser(@RequestBody UserReq user) {
        return Response.success(HttpStatus.CREATED.value(), userService.saveUser(user));
    }

    /**
     * 创建管理员用户（需要管理员权限）
     * @param adminUser 管理员用户信息
     * @return 创建成功的管理员用户信息
     */
    @PostMapping("/addAdmin")
    @SaCheckRole(value = "admin", type = "admin")
    public Response<UserResp> createAdminUser(@Valid @RequestBody UserReq adminUser) {
        return Response.success(HttpStatus.CREATED.value(), userService.saveAdminUser(adminUser));
    }

    /**
     * 获取所有用户
     * @return 所有用户列表
     */
    @GetMapping("/getAll")
    public Response<List<UserResp>> getAllUsers() {
        return Response.success(userService.getAllUsers());
    }

    /**
     * 分页获取用户
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @param username 用户名（模糊匹配）
     * @param phone 手机号（模糊匹配）
     * @return 分页用户列表
     */
    @PostMapping("/page")
    public Response<PageResult<UserResp>> pageUsers(@RequestBody UserPageReq req) {
        return Response.success(userService.pageUsers(req));
    }

    /**
     * 根据ID获取用户
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/getById/{id}")
    public Response<UserResp> getUserById(@PathVariable Long id) {
        return Response.success(userService.getUserById(id));
    }

    /**
     * 更新用户
     * @param user 用户信息
     * @return 更新后的用户信息
     */
    @PostMapping("/update")
    public Response<UserResp> updateUser(@RequestBody UserReq user) {
        return Response.success(userService.updateUser(user));
    }

    /**
     * 更新管理员用户（需要管理员权限）
     * @param adminUser 管理员用户信息
     * @return 更新后的管理员用户信息
     */
    @PostMapping("/updateAdmin")
    @SaCheckRole(value = "admin", type = "admin")
    public Response<UserResp> updateAdminUser(@Valid @RequestBody UserReq adminUser) {
        return Response.success(userService.updateAdminUser(adminUser));
    }

    /**
     * 删除用户
     * @param id 用户ID
     * @return 删除结果
     */
    @PostMapping("/deleteById/{id}")
    public Response<?> deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return Response.success();
    }

    /**
     * 删除管理员用户（需要管理员权限）
     * @param id 管理员用户ID
     * @return 删除结果
     */
    @PostMapping("/deleteAdmin/{id}")
    @SaCheckRole(value = "admin", type = "admin")
    public Response<?> deleteAdminUser(@PathVariable("id") Long id) {
        userService.deleteAdminUser(id);
        return Response.success();
    }

    /**
     * 根据用户名获取用户
     * @param username 用户名
     * @return 用户信息
     */
    @GetMapping("/getByUsername/{username}")
    public Response<UserResp> getUserByUsername(@PathVariable String username) {
        return Response.success(userService.getUserByUsername(username));
    }

    /**
     * 根据手机号获取用户
     * @param phone 手机号
     * @return 用户信息
     */
    @GetMapping("/getByPhone/{phone}")
    public Response<UserResp> getUserByPhone(@PathVariable String phone) {
        return Response.success(userService.getUserByPhone(phone));
    }

    /**
     * 分配用户角色
     * @param userId 用户ID
     * @param roleIds 角色ID集合
     * @return 操作结果
     */
    @PostMapping("/{userId}/roles")
    @SaCheckPermission(value = "system:user:assignRole", type = "admin")
    public Response<?> assignRoles(@PathVariable Long userId, @RequestBody Set<Long> roleIds) {
        userService.assignRoles(userId, roleIds);
        return Response.success();
    }

    /**
     * 移除用户角色
     * @param userId 用户ID
     * @param roleIds 角色ID集合
     * @return 操作结果
     */
    @DeleteMapping("/{userId}/roles")
    @SaCheckPermission(value = "system:user:assignRole", type = "admin")
    public Response<?> removeRoles(@PathVariable Long userId, @RequestBody Set<Long> roleIds) {
        userService.removeRoles(userId, roleIds);
        return Response.success();
    }

    /**
     * 获取用户角色
     * @param userId 用户ID
     * @return 角色标识集合
     */
    @GetMapping("/{userId}/roles")
    @SaCheckPermission(value = "system:user:query", type = "admin")
    public Response<Set<String>> getUserRoles(@PathVariable Long userId) {
        return Response.success(userService.getUserRoles(userId));
    }

    /**
     * 获取用户权限
     * @param userId 用户ID
     * @return 权限标识集合
     */
    @GetMapping("/{userId}/permissions")
    @SaCheckPermission(value = "system:user:query", type = "admin")
    public Response<Set<String>> getUserPermissions(@PathVariable Long userId) {
        return Response.success(userService.getUserPermissions(userId));
    }
}