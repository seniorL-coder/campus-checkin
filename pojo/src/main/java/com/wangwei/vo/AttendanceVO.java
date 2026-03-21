package com.wangwei.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "出勤信息")
public class AttendanceVO {

    @Schema(description = "总次数")
    private Integer totalCount;     // 总次数
    @Schema(description = "已签到")
    private Integer checkInCount;   // 已签到
    @Schema(description = "缺勤")
    private Integer absentCount;    // 缺勤
    @Schema(description = "出勤率（百分比）")
    private Double attendanceRate;    // 出勤率（百分比）
}