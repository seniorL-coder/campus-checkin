package com.wangwei.dto;

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
public class UpdateClassDTO implements Serializable {
    @Schema(description = "班级ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;
    @Schema(description = "班级名称", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String className;
    @Schema(description = "专业名称", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String major;
    @Schema(description = "更新时间", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDateTime updateTime;
}
