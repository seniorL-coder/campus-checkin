package com.wangwei.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "活动查询参数")
public class ActivityQueryDTO  extends  PageQueryDTO{
    @Schema(description = "活动标题")
    private String title;
    @Schema(description = "开始时间")
    private LocalDateTime startTime;
    @Schema(description = "结束时间")
    private LocalDateTime endTime;
    @Schema(description = "目标班级id, 逗号分隔")
    private String targetClasses;
    @Schema(description = "创建用户ID")
    private Long createUserId;
}
