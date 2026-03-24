package com.wangwei.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 签到流水实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckIn {
    private Long id;
    private Long userId;
    private Long activityId;
    private LocalDateTime checkTime;
    // 签到时的经度
    private Double lon;
    // 签到时的纬度
    private Double lat;
    private Integer status; // 签到状态:0-未开始, 1-正常, 2-迟到, 3-范围外无效
}
