package com.wangwei.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "创建签到跳转的 URL 的请求参数")
public class SignDTO {
    @Schema(description = "活动 ID")
    private Integer activityId;
}
