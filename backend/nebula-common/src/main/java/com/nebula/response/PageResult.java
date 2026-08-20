package com.nebula.response;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 分页结果
 *
 * @param <T> 记录类型
 */
@Data
public class PageResult<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 总记录数
     */
    private long total;

    /**
     * 当前页数据
     */
    private List<T> records;

    public PageResult() {
    }

    public PageResult(long total, List<T> records) {
        this.total = total;
        this.records = records;
    }

    public static <T> PageResult<T> of(long total, List<T> records) {
        return new PageResult<>(total, records);
    }
}
