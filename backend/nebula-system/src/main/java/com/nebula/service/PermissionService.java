package com.nebula.service;

import com.nebula.entity.Permission;
import com.nebula.response.PageResult;
import com.nebula.system.vo.req.PermissionPageReq;
import com.nebula.system.vo.req.PermissionReq;
import com.nebula.system.vo.resp.PermissionResp;

import java.util.List;

public interface PermissionService {
    PermissionResp savePermission(PermissionReq permissionReq);
    PermissionResp getPermissionById(Long id);
    Permission getPermissionEntityById(Long id);
    List<PermissionResp> getAllPermissions();
    PageResult<PermissionResp> pagePermissions(PermissionPageReq req);
    PermissionResp updatePermission(PermissionReq permissionReq);
    void deletePermission(Long id);
    PermissionResp getPermissionByKey(String permissionKey);
    Permission getPermissionEntityByKey(String permissionKey);
    boolean existsByPermissionKey(String permissionKey);
}
