package com.nebula.system.vo.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户信息请求Vo
 */
@Data
public class UserReq {

    private Long id;

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 1, max = 50, message = "用户名长度必须在1-50个字符之间")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, message = "密码长度至少为6个字符")
    private String password;

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 是否是管理员
     */
    private Boolean isAdmin = false;

    /**
     * 用户类型：1-后台用户，2-普通用户（不传时按 isAdmin 推导）
     */
    private Integer userType;
}