package com.wangwei.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 封装分页查询结果
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult<T> implements Serializable {
    @Schema(description = "当前页码")
    private int page;
    @Schema(description = "总页数")
    private int pages;
    @Schema(description = "每页记录数")
    private int pageSize;
    @Schema(description = "总记录数")
    private long total;
    @Schema(description = "当前页数据集合")
    private List<T> records;
}
