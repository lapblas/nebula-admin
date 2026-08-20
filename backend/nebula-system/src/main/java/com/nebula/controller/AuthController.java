package com.nebula.controller;

import com.nebula.response.Response;
import com.nebula.system.vo.req.PhoneLoginReq;
import com.nebula.system.vo.req.UserReq;
import com.nebula.system.vo.req.UsernameLoginReq;
import com.nebula.system.vo.resp.LoginResp;
import com.nebula.service.LoginService;
import com.nebula.service.UserService;
import com.nebula.system.vo.resp.UserResp;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器，处理登录、注册和登出请求
 * 登录接口按端拆分：/auth/admin/** 仅服务管理端，/auth/user/** 仅服务用户端
 */
@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final LoginService loginService;
    private final UserService userService;

    /**
     * 管理端用户名登录
     * @param loginReq 用户名登录请求参数
     * @return 登录结果
     */
    @PostMapping("/admin/login/username")
    public Response<LoginResp> adminLoginByUsername(@Valid @RequestBody UsernameLoginReq loginReq) {
        return Response.success("登录成功", loginService.adminLoginByUsername(loginReq));
    }

    /**
     * 管理端手机号登录
     * @param loginReq 手机号登录请求参数
     * @return 登录结果
     */
    @PostMapping("/admin/login/phone")
    public Response<LoginResp> adminLoginByPhone(@Valid @RequestBody PhoneLoginReq loginReq) {
        return Response.success("登录成功", loginService.adminLoginByPhone(loginReq));
    }

    /**
     * 用户端用户名登录
     * @param loginReq 用户名登录请求参数
     * @return 登录结果
     */
    @PostMapping("/user/login/username")
    public Response<LoginResp> userLoginByUsername(@Valid @RequestBody UsernameLoginReq loginReq) {
        return Response.success("登录成功", loginService.userLoginByUsername(loginReq));
    }

    /**
     * 用户端手机号登录
     * @param loginReq 手机号登录请求参数
     * @return 登录结果
     */
    @PostMapping("/user/login/phone")
    public Response<LoginResp> userLoginByPhone(@Valid @RequestBody PhoneLoginReq loginReq) {
        return Response.success("登录成功", loginService.userLoginByPhone(loginReq));
    }

    /**
     * 用户端注册（固定创建普通用户）
     * @param userReq 用户注册请求参数
     * @return 注册结果
     */
    @PostMapping("/user/register")
    public Response<UserResp> register(@Valid @RequestBody UserReq userReq) {
        // 用户端注册固定为普通用户，防止越权创建后台账号
        userReq.setIsAdmin(false);
        userReq.setUserType(2);
        return Response.success("注册成功", userService.saveUser(userReq));
    }

    /**
     * 用户登出
     * @return 登出结果
     */
    @PostMapping("/logout")
    public Response<?> logout() {
        // 调用LoginService进行登出
        loginService.logout();

        // 返回封装后的响应
        return Response.success("登出成功");
    }

    /**
     * 获取当前登录用户信息（按请求携带的 token 自动识别端）
     * @return 当前用户信息
     */
    @GetMapping("/current")
    public Response<UserResp> getCurrentUser() {
        // 调用LoginService获取当前用户信息
        return Response.success(loginService.getCurrentUser());
    }
}
