package com.nebula.system.vo.req;

import com.nebula.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色分页查询请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RolePageReq extends PageQuery {

    /**
     * 角色名称（模糊匹配）
     */
    private String roleName;

    /**
     * 角色标识（模糊匹配）
     */
    private String roleKey;
}
