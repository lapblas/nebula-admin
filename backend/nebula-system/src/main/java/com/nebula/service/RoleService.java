package com.nebula.service;

import com.nebula.entity.Role;
import com.nebula.response.PageResult;
import com.nebula.system.vo.req.RolePageReq;
import com.nebula.system.vo.req.RoleReq;
import com.nebula.system.vo.resp.RoleResp;

import java.util.List;

public interface RoleService {
    RoleResp saveRole(RoleReq roleReq);
    RoleResp getRoleById(Long id);
    Role getRoleEntityById(Long id);
    List<RoleResp> getAllRoles();
    PageResult<RoleResp> pageRoles(RolePageReq req);
    RoleResp updateRole(RoleReq roleReq);
    void deleteRole(Long id);
    RoleResp getRoleByRoleKey(String roleKey);
    Role getRoleEntityByRoleKey(String roleKey);
    boolean existsByRoleKey(String roleKey);
    boolean existsByRoleName(String roleName);
}
