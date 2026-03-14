package com.wangwei.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "添加活动DTO")
public class ActivityDTO {
    @Schema(description = "活动标题", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;
    @Schema(description = "开始时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private String startTime;
    @Schema(description = "结束时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private String endTime;
    @Schema(description = "中心点经度", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double longitude;
    @Schema(description = "中心点纬度", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double latitude;
    @Schema(description = "半径", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double radius;
    @Schema(description = "目标班级集合, 用逗号分隔, 例如: 1,2,3", requiredMode = Schema.RequiredMode.REQUIRED)
    private String targetClasses;
}
