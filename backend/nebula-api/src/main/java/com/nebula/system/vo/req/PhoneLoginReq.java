package com.nebula.system.vo.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 手机号登录请求参数Vo
 */
@Data
public class PhoneLoginReq {

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    private String phone;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;
}
