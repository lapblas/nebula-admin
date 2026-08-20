package com.nebula.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.nebula.response.PageResult;
import com.nebula.response.Response;
import com.nebula.service.PermissionService;
import com.nebula.system.vo.req.PermissionPageReq;
import com.nebula.system.vo.req.PermissionReq;
import com.nebula.system.vo.resp.PermissionResp;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permission")
@AllArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    /**
     * 创建权限
     */
    @PostMapping("/add")
    @SaCheckPermission(value = "system:permission:add", type = "admin")
    public Response<PermissionResp> createPermission(@Valid @RequestBody PermissionReq permissionReq) {
        return Response.success(HttpStatus.CREATED.value(), permissionService.savePermission(permissionReq));
    }

    /**
     * 获取所有权限
     */
    @GetMapping("/getAll")
    @SaCheckPermission(value = "system:permission:list", type = "admin")
    public Response<List<PermissionResp>> getAllPermissions() {
        return Response.success(permissionService.getAllPermissions());
    }

    /**
     * 分页获取权限
     */
    @PostMapping("/page")
    @SaCheckPermission(value = "system:permission:list", type = "admin")
    public Response<PageResult<PermissionResp>> pagePermissions(@RequestBody PermissionPageReq req) {
        return Response.success(permissionService.pagePermissions(req));
    }

    /**
     * 根据ID获取权限
     */
    @GetMapping("/getById/{id}")
    @SaCheckPermission(value = "system:permission:query", type = "admin")
    public Response<PermissionResp> getPermissionById(@PathVariable Long id) {
        return Response.success(permissionService.getPermissionById(id));
    }

    /**
     * 更新权限
     */
    @PostMapping("/update")
    @SaCheckPermission(value = "system:permission:edit", type = "admin")
    public Response<PermissionResp> updatePermission(@Valid @RequestBody PermissionReq permissionReq) {
        return Response.success(permissionService.updatePermission(permissionReq));
    }

    /**
     * 删除权限
     */
    @PostMapping("/deleteById/{id}")
    @SaCheckPermission(value = "system:permission:delete", type = "admin")
    public Response<?> deletePermission(@PathVariable("id") Long id) {
        permissionService.deletePermission(id);
        return Response.success();
    }
}
