package com.wangwei.controller.admin;

import com.wangwei.dto.ActivityDTO;
import com.wangwei.dto.ActivityQueryDTO;
import com.wangwei.dto.SignDTO;
import com.wangwei.dto.UpdateActivityDTO;
import com.wangwei.exception.IllegalArgumentException;
import com.wangwei.result.PageResult;
import com.wangwei.result.Result;
import com.wangwei.service.ActivityService;
import com.wangwei.vo.ActivityVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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

    // 修改活动状态为已结束(注意: 修改为已结束将无法再次修改)
    @Operation(summary = "修改活动状态为已结束")
    @GetMapping("/status/end/{id}")
    public Result<?> updateActivityStatusToEnd(@PathVariable Long id) {
        log.info("修改活动状态为已结束：{}", id);
        activityService.updateActivityStatusToFinished(id);
        return Result.success();
    }

    // 更新活动
    @Operation(summary = "更新活动")
    @PutMapping
    public Result<?> updateActivity(@RequestBody UpdateActivityDTO updateActivityDTO) {
        if (updateActivityDTO == null || updateActivityDTO.getId() == null) {
            throw new IllegalArgumentException("参数错误");
        }
        log.info("更新活动：{}", updateActivityDTO);
        activityService.updateActivity(updateActivityDTO);
        return Result.success();
    }

    // 初始化签到列表
    @Operation(summary = "初始化签到列表")
    @PostMapping("/initCheckIn/{id}")
    public Result<?> initCheckInList(@PathVariable Long id) {
        log.info("初始化签到列表：{}", id);
        activityService.initCheckInList(id);
        return Result.success();
    }
}
