package com.wangwei.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "学生查询参数")
public class StudentQueryDTO extends PageQueryDTO {
    @Schema(description = "教师ID")
    private Long teacherId;
    @Schema(description = "班级ID")
    private Long classId;
    @Schema(description = "学生姓名")
    private String realName;

}