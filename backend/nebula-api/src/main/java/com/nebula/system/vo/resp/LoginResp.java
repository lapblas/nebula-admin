package com.nebula.system.vo.resp;

import lombok.Data;

/**
 * 登录响应Vo，封装登录成功后的返回数据
 */
@Data
public class LoginResp {

    /**
     * 登录成功后生成的token
     */
    private String token;

    /**
     * 用户信息
     */
    private UserResp user;

}