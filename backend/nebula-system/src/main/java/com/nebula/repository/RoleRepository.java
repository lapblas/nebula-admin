package com.nebula.repository;

import com.nebula.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {

    // 查询角色
    Optional<Role> findByIdAndIsDeletedFalse(Long id);
    Optional<Role> findByRoleKeyAndIsDeletedFalse(String roleKey);
    Optional<Role> findByRoleNameAndIsDeletedFalse(String roleName);
    List<Role> findAllByIsDeletedFalse();

    // 检查角色是否存在
    boolean existsByRoleKeyAndIsDeletedFalse(String roleKey);
    boolean existsByRoleNameAndIsDeletedFalse(String roleName);
}
