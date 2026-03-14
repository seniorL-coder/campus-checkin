package com.wangwei.controller.admin;

import com.wangwei.dto.ActivityDTO;
import com.wangwei.result.Result;
import com.wangwei.service.ActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/activity")
@Slf4j
@Tag(name = "活动管理")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    @Operation(summary = "创建活动")
    @PostMapping("/create")
    public Result<String> createActivity(@RequestBody ActivityDTO activityDTO) {
        log.info("创建活动11：{}", activityDTO);
        activityService.createActivity(activityDTO);
        return Result.success("创建成功");
    }
}
