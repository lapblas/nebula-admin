package com.nebula.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.nebula.response.PageResult;
import com.nebula.response.Response;
import com.nebula.service.RoleService;
import com.nebula.system.vo.req.RolePageReq;
import com.nebula.system.vo.req.RoleReq;
import com.nebula.system.vo.resp.RoleResp;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/role")
@AllArgsConstructor
public class RoleController {

    private final RoleService roleService;

    /**
     * 创建角色
     */
    @PostMapping("/add")
    @SaCheckPermission(value = "system:role:add", type = "admin")
    public Response<RoleResp> createRole(@Valid @RequestBody RoleReq roleReq) {
        return Response.success(HttpStatus.CREATED.value(), roleService.saveRole(roleReq));
    }

    /**
     * 获取所有角色
     */
    @GetMapping("/getAll")
    @SaCheckPermission(value = "system:role:list", type = "admin")
    public Response<List<RoleResp>> getAllRoles() {
        return Response.success(roleService.getAllRoles());
    }

    /**
     * 分页获取角色
     */
    @PostMapping("/page")
    @SaCheckPermission(value = "system:role:list", type = "admin")
    public Response<PageResult<RoleResp>> pageRoles(@RequestBody RolePageReq req) {
        return Response.success(roleService.pageRoles(req));
    }

    /**
     * 根据ID获取角色
     */
    @GetMapping("/getById/{id}")
    @SaCheckPermission(value = "system:role:query", type = "admin")
    public Response<RoleResp> getRoleById(@PathVariable Long id) {
        return Response.success(roleService.getRoleById(id));
    }

    /**
     * 更新角色
     */
    @PostMapping("/update")
    @SaCheckPermission(value = "system:role:edit", type = "admin")
    public Response<RoleResp> updateRole(@Valid @RequestBody RoleReq roleReq) {
        return Response.success(roleService.updateRole(roleReq));
    }

    /**
     * 删除角色
     */
    @PostMapping("/deleteById/{id}")
    @SaCheckPermission(value = "system:role:delete", type = "admin")
    public Response<?> deleteRole(@PathVariable("id") Long id) {
        roleService.deleteRole(id);
        return Response.success();
    }
}
