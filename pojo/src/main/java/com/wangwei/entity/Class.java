package com.wangwei.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(description = "修改班级信息DTO")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Class implements Serializable {
    @Schema(description = "班级ID")
    private Long id;
    @Schema(description = "班级名称")
    private String className;
    @Schema(description = "专业名称")
    private String major;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
