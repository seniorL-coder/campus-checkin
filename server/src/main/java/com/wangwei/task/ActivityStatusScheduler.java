package com.wangwei.task;

import com.wangwei.service.ActivityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class ActivityStatusScheduler {

    private final ActivityService activityService; // 你的 Service 接口

    /**
     * 每分钟执行一次
     * cron 表达式: 秒 分 时 日 月 周
     */
    @Scheduled(cron = "0 * * * * ?")
    public void updateExpiredActivities() {
        log.info("开始执行定时更新过期活动任务: {}", LocalDateTime.now());
        try {
            // 开启 已经开始的活动
            activityService.startStartedActivities();
            // 关闭过期活动
            activityService.closeExpiredActivities();
        } catch (Exception e) {
            log.error("定时更新过期活动任务执行异常", e);
        }
    }
}