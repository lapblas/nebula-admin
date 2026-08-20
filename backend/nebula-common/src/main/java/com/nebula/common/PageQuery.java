package com.nebula.common;

import lombok.Data;

/**
 * 分页查询基础请求
 */
@Data
public class PageQuery {

    /**
     * 页码，从1开始
     */
    private Integer pageNum = 1;

    /**
     * 每页条数
     */
    private Integer pageSize = 10;
}
