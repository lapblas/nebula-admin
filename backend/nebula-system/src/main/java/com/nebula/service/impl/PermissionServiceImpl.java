package com.nebula.service.impl;

import com.nebula.entity.Permission;
import com.nebula.exception.BusinessException;
import com.nebula.repository.PermissionRepository;
import com.nebula.response.PageResult;
import com.nebula.service.PermissionService;
import com.nebula.system.vo.req.PermissionPageReq;
import com.nebula.system.vo.req.PermissionReq;
import com.nebula.system.vo.resp.PermissionResp;
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
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PermissionResp savePermission(PermissionReq permissionReq) {
        // 校验权限标识唯一性
        if (permissionRepository.existsByPermissionKeyAndIsDeletedFalse(permissionReq.getPermissionKey())) {
            throw new BusinessException(400, "权限标识已存在");
        }

        Permission permission = convertToPermission(permissionReq);
        Permission savedPermission = permissionRepository.save(permission);
        return convertToPermissionResp(savedPermission);
    }

    @Override
    public PermissionResp getPermissionById(Long id) {
        Optional<Permission> optionalPermission = permissionRepository.findByIdAndIsDeletedFalse(id);
        if (optionalPermission.isEmpty()) {
            throw new BusinessException(404, "权限不存在");
        }
        return convertToPermissionResp(optionalPermission.get());
    }

    @Override
    public Permission getPermissionEntityById(Long id) {
        Optional<Permission> optionalPermission = permissionRepository.findByIdAndIsDeletedFalse(id);
        if (optionalPermission.isEmpty()) {
            throw new BusinessException(404, "权限不存在");
        }
        return optionalPermission.get();
    }

    @Override
    public List<PermissionResp> getAllPermissions() {
        List<Permission> permissions = permissionRepository.findAllByIsDeletedFalse();
        return convertToPermissionRespList(permissions);
    }

    @Override
    public PageResult<PermissionResp> pagePermissions(PermissionPageReq req) {
        Specification<Permission> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("isDeleted"), false));
            if (StringUtils.hasText(req.getPermissionName())) {
                predicates.add(cb.like(root.get("permissionName"), "%" + req.getPermissionName().trim() + "%"));
            }
            if (StringUtils.hasText(req.getPermissionKey())) {
                predicates.add(cb.like(root.get("permissionKey"), "%" + req.getPermissionKey().trim() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        int pageNum = req.getPageNum() == null || req.getPageNum() < 1 ? 1 : req.getPageNum();
        int pageSize = req.getPageSize() == null || req.getPageSize() < 1 ? 10 : req.getPageSize();
        Page<Permission> page = permissionRepository.findAll(spec,
                PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime")));
        return PageResult.of(page.getTotalElements(), convertToPermissionRespList(page.getContent()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PermissionResp updatePermission(PermissionReq permissionReq) {
        // 校验权限是否存在
        if (permissionRepository.findByIdAndIsDeletedFalse(permissionReq.getId()).isEmpty()) {
            throw new BusinessException(404, "权限不存在");
        }
        // 校验权限标识唯一性（更新时excludePermissionId为当前权限ID）
        Optional<Permission> existingByKey = permissionRepository.findByPermissionKeyAndIsDeletedFalse(permissionReq.getPermissionKey());
        if (existingByKey.isPresent() && !existingByKey.get().getId().equals(permissionReq.getId())) {
            throw new BusinessException(400, "权限标识已存在");
        }

        Permission permission = convertToPermission(permissionReq);
        Permission updatedPermission = permissionRepository.save(permission);
        return convertToPermissionResp(updatedPermission);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePermission(Long id) {
        Permission permission = getPermissionEntityById(id);
        // 执行逻辑删除
        permission.setIsDeleted(true);
        permissionRepository.save(permission);
    }

    @Override
    public PermissionResp getPermissionByKey(String permissionKey) {
        Optional<Permission> optionalPermission = permissionRepository.findByPermissionKeyAndIsDeletedFalse(permissionKey);
        if (optionalPermission.isEmpty()) {
            throw new BusinessException(404, "权限不存在");
        }
        return convertToPermissionResp(optionalPermission.get());
    }

    @Override
    public Permission getPermissionEntityByKey(String permissionKey) {
        Optional<Permission> optionalPermission = permissionRepository.findByPermissionKeyAndIsDeletedFalse(permissionKey);
        if (optionalPermission.isEmpty()) {
            throw new BusinessException(404, "权限不存在");
        }
        return optionalPermission.get();
    }

    @Override
    public boolean existsByPermissionKey(String permissionKey) {
        return permissionRepository.existsByPermissionKeyAndIsDeletedFalse(permissionKey);
    }

    /**
     * 将PermissionReq转换为Permission实体
     */
    private Permission convertToPermission(PermissionReq permissionReq) {
        Permission permission = new Permission();
        BeanUtils.copyProperties(permissionReq, permission);
        return permission;
    }

    /**
     * 将Permission转换为PermissionResp
     */
    private PermissionResp convertToPermissionResp(Permission permission) {
        PermissionResp permissionResp = new PermissionResp();
        BeanUtils.copyProperties(permission, permissionResp);
        return permissionResp;
    }

    /**
     * 将Permission列表转换为PermissionResp列表
     */
    private List<PermissionResp> convertToPermissionRespList(List<Permission> permissions) {
        return permissions.stream()
                .map(this::convertToPermissionResp)
                .collect(Collectors.toList());
    }
}
