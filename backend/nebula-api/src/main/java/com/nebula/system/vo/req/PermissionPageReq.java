package com.nebula.system.vo.req;

import com.nebula.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 权限分页查询请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PermissionPageReq extends PageQuery {

    /**
     * 权限名称（模糊匹配）
     */
    private String permissionName;

    /**
     * 权限标识（模糊匹配）
     */
    private String permissionKey;
}
