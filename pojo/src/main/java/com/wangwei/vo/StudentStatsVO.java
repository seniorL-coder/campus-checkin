package com.wangwei.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentStatsVO {
    private Long totalCount;    // 总学生数
    private Long todayCount;    // 今日新注册学生数
}