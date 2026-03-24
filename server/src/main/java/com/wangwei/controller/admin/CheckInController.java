package com.wangwei.controller.admin;

import com.wangwei.dto.CheckInQueryDTO;
import com.wangwei.result.PageResult;
import com.wangwei.result.Result;
import com.wangwei.service.CheckInService;
import com.wangwei.vo.CheckInVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController("adminCheckInController")
@RequestMapping("/admin/checkIn")
@RequiredArgsConstructor
@Tag(name = "签到管理")
public class CheckInController {
    private final CheckInService checkInService;


    // 获取所有签到记录
    @GetMapping
    @Operation(summary = "分页获取签到记录")
    public Result<PageResult<CheckInVO>> list(CheckInQueryDTO checkInQueryDTO) {
        return Result.success(checkInService.list(checkInQueryDTO));
    }

    // 修改学生签到状态
    @GetMapping("/update")
    @Operation(summary = "修改学生签到状态")
    public Result<?> updateCheckInStatus(Long userId,
                                         Long activityId,
                                         @Schema(description = "签到状态(0: 未签到, 1: 已签到, 2: 缺勤, 3. 范围外)") Integer status) {
        checkInService.updateCheckInStatus(userId, activityId, status);
        return Result.success();
    }
}
