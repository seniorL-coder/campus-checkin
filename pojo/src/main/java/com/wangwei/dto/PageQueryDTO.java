package com.wangwei.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "分页查询参数")
public class PageQueryDTO {
    @Schema(description = "页码")
    private Integer page = 1;
    @Schema(description = "每页大小")
    private Integer limit = 10;
}
