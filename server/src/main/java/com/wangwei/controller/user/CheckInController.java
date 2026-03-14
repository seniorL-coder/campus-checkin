package com.wangwei.controller.user;

import com.wangwei.dto.CheckInDTO;
import com.wangwei.result.Result;
import com.wangwei.service.CheckInService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
