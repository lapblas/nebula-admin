package com.nebula.service.impl;

import com.nebula.entity.Role;
import com.nebula.exception.BusinessException;
import com.nebula.repository.RoleRepository;
import com.nebula.response.PageResult;
import com.nebula.service.RoleService;
import com.nebula.system.vo.req.RolePageReq;
import com.nebula.system.vo.req.RoleReq;
import com.nebula.system.vo.resp.PermissionResp;
import com.nebula.system.vo.resp.RoleResp;
import jakarta.persistence.criteria.Predicate;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RoleResp saveRole(RoleReq roleReq) {
        // 校验角色标识唯一性
        if (roleRepository.existsByRoleKeyAndIsDeletedFalse(roleReq.getRoleKey())) {
            throw new BusinessException(400, "角色标识已存在");
        }
        // 校验角色名称唯一性
        if (roleRepository.existsByRoleNameAndIsDeletedFalse(roleReq.getRoleName())) {
            throw new BusinessException(400, "角色名称已存在");
        }

        Role role = convertToRole(roleReq);
        Role savedRole = roleRepository.save(role);
        return convertToRoleResp(savedRole);
    }

    @Override
    public RoleResp getRoleById(Long id) {
        Optional<Role> optionalRole = roleRepository.findByIdAndIsDeletedFalse(id);
        if (optionalRole.isEmpty()) {
            throw new BusinessException(404, "角色不存在");
        }
        return convertToRoleResp(optionalRole.get());
    }

    @Override
    public Role getRoleEntityById(Long id) {
        Optional<Role> optionalRole = roleRepository.findByIdAndIsDeletedFalse(id);
        if (optionalRole.isEmpty()) {
            throw new BusinessException(404, "角色不存在");
        }
        return optionalRole.get();
    }

    @Override
    public List<RoleResp> getAllRoles() {
        List<Role> roles = roleRepository.findAllByIsDeletedFalse();
        return convertToRoleRespList(roles);
    }

    @Override
    public PageResult<RoleResp> pageRoles(RolePageReq req) {
        Specification<Role> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("isDeleted"), false));
            if (StringUtils.hasText(req.getRoleName())) {
                predicates.add(cb.like(root.get("roleName"), "%" + req.getRoleName().trim() + "%"));
            }
            if (StringUtils.hasText(req.getRoleKey())) {
                predicates.add(cb.like(root.get("roleKey"), "%" + req.getRoleKey().trim() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        int pageNum = req.getPageNum() == null || req.getPageNum() < 1 ? 1 : req.getPageNum();
        int pageSize = req.getPageSize() == null || req.getPageSize() < 1 ? 10 : req.getPageSize();
        Page<Role> page = roleRepository.findAll(spec,
                PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime")));
        return PageResult.of(page.getTotalElements(), convertToRoleRespList(page.getContent()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RoleResp updateRole(RoleReq roleReq) {
        // 校验角色是否存在
        if (roleRepository.findByIdAndIsDeletedFalse(roleReq.getId()).isEmpty()) {
            throw new BusinessException(404, "角色不存在");
        }
        // 校验角色标识唯一性（更新时excludeRoleId为当前角色ID）
        Optional<Role> existingByRoleKey = roleRepository.findByRoleKeyAndIsDeletedFalse(roleReq.getRoleKey());
        if (existingByRoleKey.isPresent() && !existingByRoleKey.get().getId().equals(roleReq.getId())) {
            throw new BusinessException(400, "角色标识已存在");
        }
        // 校验角色名称唯一性
        Optional<Role> existingByRoleName = roleRepository.findByRoleNameAndIsDeletedFalse(roleReq.getRoleName());
        if (existingByRoleName.isPresent() && !existingByRoleName.get().getId().equals(roleReq.getId())) {
            throw new BusinessException(400, "角色名称已存在");
        }

        Role role = convertToRole(roleReq);
        Role updatedRole = roleRepository.save(role);
        return convertToRoleResp(updatedRole);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long id) {
        Role role = getRoleEntityById(id);
        // 执行逻辑删除
        role.setIsDeleted(true);
        roleRepository.save(role);
    }

    @Override
    public RoleResp getRoleByRoleKey(String roleKey) {
        Optional<Role> optionalRole = roleRepository.findByRoleKeyAndIsDeletedFalse(roleKey);
        if (optionalRole.isEmpty()) {
            throw new BusinessException(404, "角色不存在");
        }
        return convertToRoleResp(optionalRole.get());
    }

    @Override
    public Role getRoleEntityByRoleKey(String roleKey) {
        Optional<Role> optionalRole = roleRepository.findByRoleKeyAndIsDeletedFalse(roleKey);
        if (optionalRole.isEmpty()) {
            throw new BusinessException(404, "角色不存在");
        }
        return optionalRole.get();
    }

    @Override
    public boolean existsByRoleKey(String roleKey) {
        return roleRepository.existsByRoleKeyAndIsDeletedFalse(roleKey);
    }

    @Override
    public boolean existsByRoleName(String roleName) {
        return roleRepository.existsByRoleNameAndIsDeletedFalse(roleName);
    }

    /**
     * 将RoleReq转换为Role实体
     */
    private Role convertToRole(RoleReq roleReq) {
        Role role = new Role();
        BeanUtils.copyProperties(roleReq, role);
        return role;
    }

    /**
     * 将Role转换为RoleResp
     */
    private RoleResp convertToRoleResp(Role role) {
        RoleResp roleResp = new RoleResp();
        BeanUtils.copyProperties(role, roleResp);

        // 转换权限信息
        if (role.getPermissions() != null) {
            Set<PermissionResp> permissionResps = role.getPermissions().stream()
                    .map(permission -> {
                        PermissionResp permissionResp = new PermissionResp();
                        BeanUtils.copyProperties(permission, permissionResp);
                        return permissionResp;
                    })
                    .collect(Collectors.toSet());
            roleResp.setPermissions(permissionResps);
        }

        return roleResp;
    }

    /**
     * 将Role列表转换为RoleResp列表
     */
    private List<RoleResp> convertToRoleRespList(List<Role> roles) {
        return roles.stream()
                .map(this::convertToRoleResp)
                .collect(Collectors.toList());
    }
}
