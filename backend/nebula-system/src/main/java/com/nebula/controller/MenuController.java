package com.nebula.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.nebula.config.StpAdminUtil;
import com.nebula.response.Response;
import com.nebula.service.MenuService;
import com.nebula.system.vo.req.MenuReq;
import com.nebula.system.vo.req.MenuTreeReq;
import com.nebula.system.vo.resp.MenuResp;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menu")
@AllArgsConstructor
public class MenuController {

    private final MenuService menuService;

    /**
     * 创建菜单
     */
    @PostMapping("/add")
    @SaCheckPermission(value = "system:menu:add", type = "admin")
    public Response<MenuResp> createMenu(@Valid @RequestBody MenuReq menuReq) {
        return Response.success(HttpStatus.CREATED.value(), menuService.saveMenu(menuReq));
    }

    /**
     * 获取所有菜单（平铺）
     */
    @GetMapping("/getAll")
    @SaCheckPermission(value = "system:menu:list", type = "admin")
    public Response<List<MenuResp>> getAllMenus() {
        return Response.success(menuService.getAllMenus());
    }

    /**
     * 获取菜单树（全部）
     * @param menuName 菜单名称（模糊匹配）
     * @param path 路由路径（模糊匹配）
     */
    @PostMapping("/tree")
    @SaCheckPermission(value = "system:menu:list", type = "admin")
    public Response<List<MenuResp>> getMenuTree(@RequestBody(required = false) MenuTreeReq req) {
        return Response.success(menuService.getMenuTree(req));
    }

    /**
     * 获取当前用户的菜单树
     */
    @GetMapping("/user/tree")
    public Response<List<MenuResp>> getUserMenuTree() {
        Long userId = StpAdminUtil.getLoginIdAsLong();
        return Response.success(menuService.getUserMenuTree(userId));
    }

    /**
     * 根据ID获取菜单
     */
    @GetMapping("/getById/{id}")
    @SaCheckPermission(value = "system:menu:query", type = "admin")
    public Response<MenuResp> getMenuById(@PathVariable Long id) {
        return Response.success(menuService.getMenuById(id));
    }

    /**
     * 更新菜单
     */
    @PostMapping("/update")
    @SaCheckPermission(value = "system:menu:edit", type = "admin")
    public Response<MenuResp> updateMenu(@Valid @RequestBody MenuReq menuReq) {
        return Response.success(menuService.updateMenu(menuReq));
    }

    /**
     * 删除菜单
     */
    @PostMapping("/deleteById/{id}")
    @SaCheckPermission(value = "system:menu:delete", type = "admin")
    public Response<?> deleteMenu(@PathVariable("id") Long id) {
        menuService.deleteMenu(id);
        return Response.success();
    }
}
