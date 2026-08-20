package com.nebula.system.vo.req;

import com.nebula.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户分页查询请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserPageReq extends PageQuery {

    /**
     * 用户名（模糊匹配）
     */
    private String username;

    /**
     * 手机号（模糊匹配）
     */
    private String phone;
}
