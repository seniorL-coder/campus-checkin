package com.wangwei.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "活动VO")
public class ActivityVO implements Serializable {
    @Schema(description = "活动ID")
    private Long id;
    @Schema(description = "活动标题")
    private String title;
    @Schema(description = "开始时间")
    private LocalDateTime startTime;
    @Schema(description = "结束时间")
    private LocalDateTime endTime;
    @Schema(description = "经度")
    private Double longitude;
    @Schema(description = "纬度")
    private Double latitude;
    @Schema(description = "半径")
    private Integer radius;
    @Schema(description = "目标班级名称")
    private String targetClassesName;
    @Schema(description = "目标班级ID")
    private String targetClasses;
    @Schema(description = "创建用户ID")
    private Long createUserId;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}
