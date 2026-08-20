package com.nebula.service.impl;

import cn.dev33.satoken.stp.StpLogic;
import com.nebula.config.StpAdminUtil;
import com.nebula.config.StpUserUtil;
import com.nebula.entity.User;
import com.nebula.system.enums.ClientType;
import com.nebula.system.vo.req.PhoneLoginReq;
import com.nebula.system.vo.req.UsernameLoginReq;
import com.nebula.system.vo.resp.LoginResp;
import com.nebula.system.vo.resp.UserResp;
import com.nebula.exception.BusinessException;
import com.nebula.utils.CryptoUtils;
import com.nebula.utils.RsaUtils;
import com.nebula.config.RsaKeyConfig;
import com.nebula.service.LoginService;
import com.nebula.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 登录服务实现类，实现具体的登录业务逻辑
 */
@Service
@Slf4j
@AllArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final UserService userService;
    private final RsaKeyConfig rsaKeyConfig;

    @Override
    public LoginResp adminLoginByUsername(UsernameLoginReq loginReq) throws BusinessException {
        User user = userService.getUserEntityByUsername(loginReq.getUsername());
        return doLogin(user, decryptPassword(loginReq.getPassword()), ClientType.ADMIN);
    }

    @Override
    public LoginResp adminLoginByPhone(PhoneLoginReq loginReq) throws BusinessException {
        User user = userService.getUserEntityByPhone(loginReq.getPhone());
        return doLogin(user, decryptPassword(loginReq.getPassword()), ClientType.ADMIN);
    }

    @Override
    public LoginResp userLoginByUsername(UsernameLoginReq loginReq) throws BusinessException {
        User user = userService.getUserEntityByUsername(loginReq.getUsername());
        return doLogin(user, decryptPassword(loginReq.getPassword()), ClientType.USER);
    }

    @Override
    public LoginResp userLoginByPhone(PhoneLoginReq loginReq) throws BusinessException {
        User user = userService.getUserEntityByPhone(loginReq.getPhone());
        return doLogin(user, decryptPassword(loginReq.getPassword()), ClientType.USER);
    }

    /**
     * 解密密码
     * @param encryptedPassword 加密后的密码
     * @return 解密后的密码
     * @throws BusinessException 业务异常
     */
    private String decryptPassword(String encryptedPassword) throws BusinessException {
        try {
            if (rsaKeyConfig.isEnable()) {
                return RsaUtils.decrypt(encryptedPassword, rsaKeyConfig.getPrivateKey());
            } else {
                return encryptedPassword;
            }
        } catch (Exception e) {
            log.error("密码解密失败：{}", e.getMessage(), e);
            throw new BusinessException(400, "密码格式错误", e);
        }
    }

    /**
     * 执行登录操作
     * @param user 用户实体
     * @param password 密码
     * @param clientType 客户端类型，决定账号身份校验与所属登录体系
     * @return 登录结果
     * @throws BusinessException 业务异常
     */
    private LoginResp doLogin(User user, String password, ClientType clientType) throws BusinessException {
        // 校验账号身份类型与当前端是否匹配
        validateUserType(user, clientType);

        // 验证密码（使用加密验证）
        if (!CryptoUtils.matchesPassword(password, user.getPassword())) {
            throw new BusinessException(401, "密码错误");
        }

        // 转换为 UserResp
        UserResp userResp = convertToUserResp(user);

        // 登录成功，按客户端类型使用对应的账号体系生成token
        log.info("准备登录用户: {}", user.getId());
        String token;
        try {
            StpLogic stpLogic = clientType == ClientType.ADMIN ? StpAdminUtil.stpLogic : StpUserUtil.stpLogic;
            stpLogic.login(user.getId());
            token = stpLogic.getTokenValue();
            log.debug("登录成功，生成token: {}", token);

            // 构建登录结果数据
            LoginResp result = new LoginResp();
            result.setToken(token);
            result.setUser(userResp);

            return result;
        } catch (Exception e) {
            log.error("登录失败: {}", e.getMessage(), e);
            throw new BusinessException(500, "登录失败: " + e.getMessage(), e);
        }
    }

    @Override
    public void logout() {
        // 同时登出所有账号体系
        StpAdminUtil.logout();
        StpUserUtil.logout();
    }

    @Override
    public UserResp getCurrentUser() throws BusinessException {
        // 从统一请求头读取 token（已剥离前缀），按 token 内嵌的 loginType 前缀定位所属端
        String token = StpAdminUtil.stpLogic.getTokenValue();
        if (token == null || token.isEmpty()) {
            throw new BusinessException(401, "未登录或登录已过期");
        }
        ClientType clientType = ClientType.parseFromToken(token);
        if (clientType == null) {
            throw new BusinessException(401, "未登录或登录已过期");
        }

        StpLogic stpLogic = clientType == ClientType.ADMIN ? StpAdminUtil.stpLogic : StpUserUtil.stpLogic;
        if (!stpLogic.isLogin()) {
            throw new BusinessException(401, "未登录或登录已过期");
        }

        // 根据id查询用户
        return userService.getUserById(stpLogic.getLoginIdAsLong());
    }

    /**
     * 将User实体转换为UserResp对象
     * @param user User实体
     * @return UserResp对象
     */
    private UserResp convertToUserResp(com.nebula.entity.User user) {
        UserResp userResp = new UserResp();
        userResp.setId(user.getId());
        userResp.setUsername(user.getUsername());
        userResp.setPhone(user.getPhone());
        userResp.setCreateTime(user.getCreateTime());
        userResp.setUpdateTime(user.getUpdateTime());
        userResp.setIsAdmin(user.getIsAdmin());
        userResp.setUserType(user.getUserType() == null ? 1 : user.getUserType());
        // 不设置密码字段
        return userResp;
    }

    /**
     * 验证账号身份类型与客户端类型是否匹配
     * @param user 用户实体
     * @param clientType 客户端类型
     * @throws BusinessException 业务异常
     */
    private void validateUserType(User user, ClientType clientType) throws BusinessException {
        // 历史数据 user_type 为空时按后台用户(1)处理
        Integer userType = user.getUserType() == null ? 1 : user.getUserType();
        if (!clientType.getAllowedUserTypes().contains(userType)) {
            if (clientType == ClientType.ADMIN) {
                throw new BusinessException(403, "非后台用户不能登录管理后台");
            } else {
                throw new BusinessException(403, "非普通用户不能登录用户端");
            }
        }
    }
}
