package com.wangwei.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "签到查询参数")
public class CheckInQueryDTO extends PageQueryDTO {
    @Schema(description = "学生姓名")
    private String userName;
    @Schema(description = "活动名称")
    private String activityTitle;
}
