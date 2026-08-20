package com.nebula.service.impl;

import com.nebula.system.vo.req.UserPageReq;
import com.nebula.system.vo.req.UserReq;
import com.nebula.system.vo.resp.RoleResp;
import com.nebula.system.vo.resp.UserResp;
import com.nebula.exception.BusinessException;
import com.nebula.utils.RsaUtils;
import com.nebula.config.RsaKeyConfig;
import com.nebula.entity.Permission;
import com.nebula.entity.Role;
import com.nebula.entity.User;
import com.nebula.repository.RoleRepository;
import com.nebula.repository.UserRepository;
import com.nebula.response.PageResult;
import com.nebula.service.UserService;
import com.nebula.utils.CryptoUtils;
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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RsaKeyConfig rsaKeyConfig;

    /**
     * 将UserReq转换为User
     * @param userReq 用户请求对象
     * @return 用户实体对象
     */
    private User convertToUser(UserReq userReq) {
        String password;
        try {
            // RSA 解密
            if (rsaKeyConfig.isEnable()) {
                password = RsaUtils.decrypt(userReq.getPassword(), rsaKeyConfig.getPrivateKey());
            } else {
                password = userReq.getPassword();
            }
        } catch (Exception e) {
            log.error("密码解密失败: {}", e.getMessage(), e);
            throw new BusinessException(400, "密码格式错误", e);
        }
        // 密码加盐加密存储
        userReq.setPassword(CryptoUtils.encryptPassword(password));
        User user = new User();
        BeanUtils.copyProperties(userReq, user);
        return user;
    }

    /**
     * 将User转换为UserResp
     * @param user 用户实体对象
     * @return 用户响应对象
     */
    private UserResp convertToUserResp(User user) {
        UserResp userResp = new UserResp();
        BeanUtils.copyProperties(user, userResp);

        // 转换角色信息
        if (user.getRoles() != null) {
            Set<RoleResp> roleResps = user.getRoles().stream()
                    .map(role -> {
                        RoleResp roleResp = new RoleResp();
                        BeanUtils.copyProperties(role, roleResp);
                        return roleResp;
                    })
                    .collect(Collectors.toSet());
            userResp.setRoles(roleResps);
        }

        return userResp;
    }

    /**
     * 将User列表转换为UserResp列表
     * @param users 用户实体列表
     * @return 用户响应列表
     */
    private List<UserResp> convertToUserRespList(List<User> users) {
        List<UserResp> userRespList = new ArrayList<>();
        for (User user : users) {
            userRespList.add(convertToUserResp(user));
        }
        return userRespList;
    }

    /**
     * 校验用户唯一性（支持创建和更新场景）
     * @param userReq 用户请求对象
     * @param excludeUserId 需要排除的用户ID（更新时传入当前用户ID，创建时传null）
     */
    private void validateUserUniqueness(UserReq userReq, Long excludeUserId) {
        // 如果是更新操作，需要获取当前用户信息进行比较
        if (excludeUserId != null) {
            User currentUser = getUserEntityById(excludeUserId);
            // 校验用户名唯一性（排除当前用户）
            if (!currentUser.getUsername().equals(userReq.getUsername()) && existsByUsername(userReq.getUsername())) {
                throw new BusinessException(400, "用户名已存在");
            }
            // 校验手机号唯一性（排除当前用户）
            if (!currentUser.getPhone().equals(userReq.getPhone()) && existsByPhone(userReq.getPhone())) {
                throw new BusinessException(400, "手机号已存在");
            }
        } else {
            // 创建操作，直接校验所有字段
            if (existsByUsername(userReq.getUsername())) {
                throw new BusinessException(400, "用户名已存在");
            }
            if (existsByPhone(userReq.getPhone())) {
                throw new BusinessException(400, "手机号已存在");
            }
        }
    }

    /**
     * 根据用户名查询用户实体
     * @param username 用户名
     * @return 用户实体
     */
    private User findUserByUsername(String username) {
        Optional<User> user = userRepository.findByUsernameAndIsDeletedFalse(username);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new BusinessException(404, "用户不存在");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserResp saveUser(UserReq userReq) {
        // 校验用户唯一性（创建时excludeUserId为null）
        validateUserUniqueness(userReq, null);

        // 未指定用户类型时，按是否管理员推导：管理员为后台用户(1)，否则为普通用户(2)
        if (userReq.getUserType() == null) {
            userReq.setUserType(Boolean.TRUE.equals(userReq.getIsAdmin()) ? 1 : 2);
        }
        // 仅后台用户可设置为管理员
        if (Boolean.TRUE.equals(userReq.getIsAdmin()) && !Integer.valueOf(1).equals(userReq.getUserType())) {
            throw new BusinessException(400, "只有后台用户才能设置为管理员");
        }

        User user = convertToUser(userReq);
        User savedUser = userRepository.save(user);
        return convertToUserResp(savedUser);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserResp saveAdminUser(UserReq adminUserReq) {
        // 设置为管理员，且为后台用户
        adminUserReq.setIsAdmin(true);
        adminUserReq.setUserType(1);
        return saveUser(adminUserReq);
    }

    @Override
    public UserResp getUserById(Long id) {
        Optional<User> optionalUser = userRepository.findByIdAndIsDeletedFalse(id);
        if (optionalUser.isEmpty()) {
            throw new BusinessException(404, "用户不存在");
        }
        return convertToUserResp(optionalUser.get());
    }

    @Override
    public User getUserEntityById(Long id) {
        // 从数据库获取
        Optional<User> optionalUser = userRepository.findByIdAndIsDeletedFalse(id);
        if (optionalUser.isEmpty()) {
            throw new BusinessException(404, "用户不存在");
        }
        return optionalUser.get();
    }

    @Override
    public List<UserResp> getAllUsers() {
        List<User> users = userRepository.findAllByIsDeletedFalse();
        return convertToUserRespList(users);
    }

    @Override
    public PageResult<UserResp> pageUsers(UserPageReq req) {
        Specification<User> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("isDeleted"), false));
            if (StringUtils.hasText(req.getUsername())) {
                predicates.add(cb.like(root.get("username"), "%" + req.getUsername().trim() + "%"));
            }
            if (StringUtils.hasText(req.getPhone())) {
                predicates.add(cb.like(root.get("phone"), "%" + req.getPhone().trim() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        int pageNum = req.getPageNum() == null || req.getPageNum() < 1 ? 1 : req.getPageNum();
        int pageSize = req.getPageSize() == null || req.getPageSize() < 1 ? 10 : req.getPageSize();
        Page<User> page = userRepository.findAll(spec,
                PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime")));
        return PageResult.of(page.getTotalElements(), convertToUserRespList(page.getContent()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserResp updateUser(UserReq userReq) {
        // 校验用户是否存在
        if (userRepository.findByIdAndIsDeletedFalse(userReq.getId()).isEmpty()) {
            throw new BusinessException(404, "用户不存在");
        }
        // 校验用户唯一性（更新时excludeUserId为当前用户ID）
        validateUserUniqueness(userReq, userReq.getId());
        User user = convertToUser(userReq);
        // 未修改用户类型时保持原值
        if (user.getUserType() == null) {
            user.setUserType(getUserEntityById(userReq.getId()).getUserType());
        }
        // 仅后台用户可设置为管理员
        if (Boolean.TRUE.equals(user.getIsAdmin()) && !Integer.valueOf(1).equals(user.getUserType())) {
            throw new BusinessException(400, "只有后台用户才能设置为管理员");
        }
        User updatedUser = userRepository.save(user);
        return convertToUserResp(updatedUser);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserResp updateAdminUser(UserReq adminUserReq) {
        // 验证用户是否是管理员
        User existingUser = getUserEntityById(adminUserReq.getId());
        if (!Boolean.TRUE.equals(existingUser.getIsAdmin())) {
            throw new BusinessException(403, "只能更新管理员用户");
        }

        // 保持管理员状态
        adminUserReq.setIsAdmin(true);
        adminUserReq.setUserType(1);
        return updateUser(adminUserReq);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAdminUser(Long id) {
        // 验证用户是否是管理员
        User existingUser = getUserEntityById(id);
        if (!Boolean.TRUE.equals(existingUser.getIsAdmin())) {
            throw new BusinessException(403, "只能删除管理员用户");
        }

        // 执行逻辑删除
        existingUser.setIsDeleted(true);
        userRepository.save(existingUser);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        Optional<User> existingUser = userRepository.findByIdAndIsDeletedFalse(id);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            user.setIsDeleted(true);
            userRepository.save(user);
        } else {
            throw new BusinessException(404, "用户不存在");
        }
    }

    @Override
    public UserResp getUserByUsername(String username) {
        User user = findUserByUsername(username);
        return convertToUserResp(user);
    }

    @Override
    public User getUserEntityByUsername(String username) {
        return findUserByUsername(username);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsernameAndIsDeletedFalse(username);
    }

    @Override
    public UserResp getUserByPhone(String phone) {
        Optional<User> user = userRepository.findByPhoneAndIsDeletedFalse(phone);
        if (user.isPresent()) {
            return convertToUserResp(user.get());
        } else {
            throw new BusinessException(404, "用户不存在");
        }
    }

    @Override
    public User getUserEntityByPhone(String phone) {
        Optional<User> user = userRepository.findByPhoneAndIsDeletedFalse(phone);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new BusinessException(404, "用户不存在");
        }
    }

    @Override
    public boolean existsByPhone(String phone) {
        return userRepository.existsByPhoneAndIsDeletedFalse(phone);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(Long userId, Set<Long> roleIds) {
        User user = getUserEntityById(userId);
        Set<Role> roles = new HashSet<>(roleRepository.findAllById(roleIds));
        if (roles.size() != roleIds.size()) {
            throw new BusinessException(400, "部分角色不存在");
        }
        user.setRoles(roles);
        userRepository.save(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeRoles(Long userId, Set<Long> roleIds) {
        User user = getUserEntityById(userId);
        user.getRoles().removeIf(role -> roleIds.contains(role.getId()));
        userRepository.save(user);
    }

    @Override
    public Set<String> getUserRoles(Long userId) {
        User user = getUserEntityById(userId);
        return user.getRoles().stream()
                .map(Role::getRoleKey)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<String> getUserPermissions(Long userId) {
        User user = getUserEntityById(userId);
        return user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getPermissionKey)
                .collect(Collectors.toSet());
    }
}