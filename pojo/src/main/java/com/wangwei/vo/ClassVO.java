package com.wangwei.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "班级信息", requiredMode = Schema.RequiredMode.REQUIRED)
public class ClassVO implements Serializable {
    @Schema(description = "班级ID")
    private Long id;
    @Schema(description = "班级名称")
    private String className;
    @Schema(description = "教师ID")
    private Integer teacherId;
    @Schema(description = "教师名称")
    private String teacherName;
    @Schema(description = "专业")
    private String major;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
