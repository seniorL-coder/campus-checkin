package com.wangwei.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "修改学生信息请求参数")
public class UpdateStudentDTO {

    @Schema(description = "学生ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "真实姓名")
    private String realName;

    @Schema(description = "登录账号")
    private String username;

    @Schema(description = "班级ID")
    private Long classId;
}
