package com.wangwei.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "通知消息")
public class NotificationVO {

    @Schema(description = "通知ID")
    private Long id;
    @Schema(description = "活动ID")
    private Long activityId;
    @Schema(description = "通知内容")
    private String content;

    @Schema(description = "是否已读")
    private Integer isRead;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    @Schema(description = "阅读时间")
    private LocalDateTime readTime;
}