package com.wangwei.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Notification {
    private Long id;
    // userId
    private Long userId;
    // notification content
    private String content;
    // 活动ID
    private Long activityId;
    // 是否已读
    private Integer isRead; // 0-未读, 1-已读
    // 创建时间
    private LocalDateTime createTime;
    // 消息读取时间
    private LocalDateTime readTime;
}
