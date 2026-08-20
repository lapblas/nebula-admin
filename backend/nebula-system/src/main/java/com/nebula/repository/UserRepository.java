package com.nebula.repository;

import com.nebula.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    // 查询用户
    Optional<User> findByIdAndIsDeletedFalse(Long id);
    Optional<User> findByUsernameAndIsDeletedFalse(String username);
    Optional<User> findByPhoneAndIsDeletedFalse(String phone);
    List<User> findAllByIsDeletedFalse();
    
    // 检查用户是否存在
    boolean existsByUsernameAndIsDeletedFalse(String username);
    boolean existsByPhoneAndIsDeletedFalse(String phone);
}