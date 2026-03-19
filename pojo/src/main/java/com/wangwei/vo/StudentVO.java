package com.wangwei.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Schema(description = "用户VO")
public class StudentVO extends UserVO implements Serializable {
    @Schema(description = "班级名称")
    private String className;
    @Schema(description = "班级ID")
    private Long classId;
    @Schema(description = "令牌")
    private String token;
    @Schema(description = "老师名称")
    private String teacherName;

}
