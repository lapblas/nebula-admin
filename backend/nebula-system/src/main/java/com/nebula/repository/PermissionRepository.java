package com.nebula.repository;

import com.nebula.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long>, JpaSpecificationExecutor<Permission> {

    // 查询权限
    Optional<Permission> findByIdAndIsDeletedFalse(Long id);
    Optional<Permission> findByPermissionKeyAndIsDeletedFalse(String permissionKey);
    List<Permission> findAllByIsDeletedFalse();

    // 检查权限是否存在
    boolean existsByPermissionKeyAndIsDeletedFalse(String permissionKey);
}
