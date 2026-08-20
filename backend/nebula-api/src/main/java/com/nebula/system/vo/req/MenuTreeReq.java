package com.nebula.system.vo.req;

import lombok.Data;

/**
 * 菜单树查询请求
 */
@Data
public class MenuTreeReq {

    /**
     * 菜单名称（模糊匹配）
     */
    private String menuName;

    /**
     * 路由路径（模糊匹配）
     */
    private String path;
}
