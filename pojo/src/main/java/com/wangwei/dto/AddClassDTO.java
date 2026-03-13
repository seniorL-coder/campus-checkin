package com.wangwei.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Schema(description = "添加班级信息DTO")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddClassDTO implements Serializable {

    @Schema(description = "班级名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String className;
    @Schema(description = "专业名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String major;
    @Schema(description = "教师id", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer teacherId;

}
