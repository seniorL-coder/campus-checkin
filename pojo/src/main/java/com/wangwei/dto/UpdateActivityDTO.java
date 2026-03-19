package com.wangwei.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "修改活动请求参数")
public class UpdateActivityDTO {

    @Schema(description = "活动ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "活动标题")
    private String title;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

}