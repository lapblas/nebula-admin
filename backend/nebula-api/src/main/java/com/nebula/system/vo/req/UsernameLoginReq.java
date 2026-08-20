package com.nebula.system.vo.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户名登录请求参数Vo
 */
@Data
public class UsernameLoginReq {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;
}
