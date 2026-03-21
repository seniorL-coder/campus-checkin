package com.wangwei.controller.user;

import com.wangwei.result.Result;
import com.wangwei.service.NotificationService;
import com.wangwei.vo.NotificationVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/notification")
@Slf4j
@Tag(name = "站内消息通知")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * 获取未读消息数量（红点）
     */
    @GetMapping("/unread/count")
    public Result<Integer> getUnreadCount() {
        return Result.success(notificationService.getUnreadCount());
    }

    /**
     * 获取消息列表
     */
    @GetMapping("/list")
    public Result<List<NotificationVO>> getList() {
        return Result.success(notificationService.getList());
    }

    /**
     * 标记全部已读
     */
    @PutMapping("/read/all")
    public Result<?> readAll() {
        notificationService.readAll();
        return Result.success();
    }

    /**
     * 标记单条已读
     */
    @PutMapping("/read/{id}")
    public Result<?> readOne(@PathVariable Long id) {
        notificationService.readOne(id);
        return Result.success();
    }
}