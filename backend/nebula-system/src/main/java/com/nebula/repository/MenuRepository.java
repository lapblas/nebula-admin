package com.nebula.repository;

import com.nebula.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long>, JpaSpecificationExecutor<Menu> {

    List<Menu> findAllByIsDeletedFalseOrderBySortOrder();

    List<Menu> findAllByParentIdAndIsDeletedFalseOrderBySortOrder(Long parentId);

    List<Menu> findAllByPermissionKeyInAndIsDeletedFalse(List<String> permissionKeys);

    boolean existsByPermissionKeyAndIsDeletedFalse(String permissionKey);
}
