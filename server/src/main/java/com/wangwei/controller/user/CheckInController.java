package com.wangwei.controller.user;

import com.wangwei.dto.CheckInDTO;
import com.wangwei.result.Result;
import com.wangwei.service.CheckInService;
import com.wangwei.vo.CheckInVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user/checkIn")
@Slf4j
@Tag(name = "签到管理")
public class CheckInController {

    private final CheckInService checkInService;

    public CheckInController(CheckInService checkInService) {
        this.checkInService = checkInService;
    }

    @Operation(summary = "签到功能")
    @GetMapping
    public Result<?> checkIn(CheckInDTO checkInDTO) {
        log.info("签到信息：{}", checkInDTO);
        checkInService.checkIn(checkInDTO);
        return Result.success("签到成功");
    }

    /**
     * 获取今日签到日程
     */
    @Operation(summary = "获取今日签到日程")
    @GetMapping("/today")
    public Result<List<CheckInVO>> getTodaySchedule() {
        return Result.success(checkInService.getTodaySchedule());
    }

    /**
     * 获取全部签到记录（未开始, 已完成, 未完成）
     */
    @Operation(summary = "获取签到记录列表, 0->未签到, 1->正常, 2->缺勤")
    @GetMapping("/records")
    public Result<List<CheckInVO>> getCheckInRecords(Integer status) {
        return Result.success(checkInService.getCheckInRecords(status));
    }

}
