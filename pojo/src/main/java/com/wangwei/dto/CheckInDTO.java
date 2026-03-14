package com.wangwei.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "签到信息", requiredMode = Schema.RequiredMode.REQUIRED)
public class CheckInDTO {
    @Schema(description = "活动ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long activityId;
    @Schema(description = "链接的动态 token", requiredMode = Schema.RequiredMode.REQUIRED)
    private String token;
    @Schema(description = "经度", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double lon;
    @Schema(description = "纬度", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double lat;
}
