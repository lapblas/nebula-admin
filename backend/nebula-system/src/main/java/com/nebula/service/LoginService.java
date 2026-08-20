package com.nebula.service;

import com.nebula.exception.BusinessException;
import com.nebula.system.vo.req.PhoneLoginReq;
import com.nebula.system.vo.req.UsernameLoginReq;
import com.nebula.system.vo.resp.LoginResp;
import com.nebula.system.vo.resp.UserResp;

/**
 * 登录服务接口，定义登录相关的方法
 */
public interface LoginService {

    /**
     * 管理端用户名登录
     * @param loginReq 用户名登录请求参数
     * @return 登录结果，包含token和用户信息
     * @throws BusinessException 业务异常
     */
    LoginResp adminLoginByUsername(UsernameLoginReq loginReq) throws BusinessException;

    /**
     * 管理端手机号登录
     * @param loginReq 手机号登录请求参数
     * @return 登录结果，包含token和用户信息
     * @throws BusinessException 业务异常
     */
    LoginResp adminLoginByPhone(PhoneLoginReq loginReq) throws BusinessException;

    /**
     * 用户端用户名登录
     * @param loginReq 用户名登录请求参数
     * @return 登录结果，包含token和用户信息
     * @throws BusinessException 业务异常
     */
    LoginResp userLoginByUsername(UsernameLoginReq loginReq) throws BusinessException;

    /**
     * 用户端手机号登录
     * @param loginReq 手机号登录请求参数
     * @return 登录结果，包含token和用户信息
     * @throws BusinessException 业务异常
     */
    LoginResp userLoginByPhone(PhoneLoginReq loginReq) throws BusinessException;

    /**
     * 用户登出（同时登出所有账号体系）
     * @return 登出结果
     */
    void logout();

    /**
     * 获取当前登录用户信息（按请求携带的 token 自动识别管理端/用户端）
     * @return 当前用户信息
     * @throws BusinessException 业务异常
     */
    UserResp getCurrentUser() throws BusinessException;
}
