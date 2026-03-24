package com.wangwei.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "签到信息")
public class CheckInVO {

    @Schema(description = "签到ID")
    private Long id;
    @Schema(description = "活动ID")
    private Long activityId;
    @Schema(description = "学生ID")
    private Long userId;
    @Schema(description = "学生姓名")
    private String userName;
    @Schema(description = "活动标题")
    private String activityTitle;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;
    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "签到时间")
    private LocalDateTime checkTime;

    @Schema(description = "签到状态, 0-未签到, 1-已签到, 2-缺勤, 3-范围外")
    private Integer status;
    // 活动状态
    @Schema(description = "活动状态, 0-未开始, 1-进行中, 2-已结束")
    private Integer activityStatus;
    @Schema(description = "签到位置经度")
    private Double longitude;
    @Schema(description = "签到位置纬度")
    private Double latitude;
}