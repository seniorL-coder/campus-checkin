package com.wangwei.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "用户VO")
public class StudentVO extends UserVO implements Serializable {
    @Schema(description = "班级名称")
    private String className;

}
