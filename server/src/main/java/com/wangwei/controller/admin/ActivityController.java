package com.wangwei.controller.admin;

import com.wangwei.dto.ActivityDTO;
import com.wangwei.dto.ActivityQueryDTO;
import com.wangwei.dto.SignDTO;
import com.wangwei.result.PageResult;
import com.wangwei.result.Result;
import com.wangwei.service.ActivityService;
import com.wangwei.vo.ActivityVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
        log.info("创建活动：{}", activityDTO);
        activityService.createActivity(activityDTO);
        return Result.success("创建成功");
    }
    // 创建活动签到的 url 跳转连接接口
    @Operation(summary = "创建活动签到跳转的 url")
    @PostMapping("/sign")
    public Result<String> createActivitySign(@RequestBody SignDTO signDTO) {
        log.info("创建活动签到：{}", signDTO);
        String url = activityService.createActivitySign(signDTO);
        return Result.success(url);
    }
    // 分页查询活动列表
    @Operation(summary = "分页查询活动列表")
    @PostMapping("/list")
    public Result<PageResult<ActivityVO>> list(@RequestBody ActivityQueryDTO activityQueryDTO) {
        log.info("分页查询活动列表:{}", activityQueryDTO);
        PageResult<ActivityVO> activityVOPageResult = activityService.list(activityQueryDTO);
        return Result.success(activityVOPageResult);
    }
}
