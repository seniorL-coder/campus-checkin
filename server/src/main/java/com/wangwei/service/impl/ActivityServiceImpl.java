package com.wangwei.service.impl;

import com.wangwei.context.BaseContext;
import com.wangwei.dto.ActivityDTO;
import com.wangwei.entity.Activity;
import com.wangwei.entity.CheckIn;
import com.wangwei.entity.Notification;
import com.wangwei.mapper.ActivityMapper;
import com.wangwei.mapper.CheckInMapper;
import com.wangwei.mapper.NotificationMapper;
import com.wangwei.service.ActivityService;
import com.wangwei.service.UserService;
import com.wangwei.vo.UserVO;
import com.wangwei.websocket.UserWebSocketServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityServiceImpl implements ActivityService {
    private final ActivityMapper activityMapper;
    private final UserService userService;
    private final CheckInMapper checkInMapper;
    private final NotificationMapper notificationMapper;
    private final UserWebSocketServer userWebSocketServer;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createActivity(ActivityDTO activityDTO) {
        log.info("创建活动: {}", activityDTO);
        // 1. 创建活动 本体
        Activity activity = Activity.builder()
                .title(activityDTO.getTitle())
                .startTime(activityDTO.getStartTime())
                .endTime(activityDTO.getEndTime())
                .longitude(activityDTO.getLongitude())
                .latitude(activityDTO.getLatitude())
                .radius(activityDTO.getRadius())
                .targetClasses(activityDTO.getTargetClasses())
                .createUserId(BaseContext.getCurrentId().intValue())
                .build();
        activityMapper.insert(activity);
        // 2. 查询班级id下的所有学生, 添加到 签到流水中 和 消息通知表中, 注意事务一致性
        //2.1 查询学生列表
        List<Integer> targetClassesIds = Stream.of(activityDTO.getTargetClasses().split(",")).map(Integer::parseInt).toList();
        List<UserVO> students = userService.getStudentsByClassIds(targetClassesIds);
        List<Long> studentIds = students.stream().map(UserVO::getId).toList();
        //2.2 添加到签到流水中
        // 2.2.1 构建签到数据 List,
        List<CheckIn> checkIns = studentIds.stream().map(id -> CheckIn.builder()
                .userId(id)
                .activityId(activity.getId())
                .status(0)
                .build()).toList();
        // 2.2.2 批量插入签到数据
        checkInMapper.insertCheckIns(checkIns);
        // 2.3 添加到消息通知表中
        // 2.3.1 构建消息通知数据 List,
        List<Notification> notifications = studentIds.stream().map(id -> Notification.builder()
                .userId(id)
                .activityId(activity.getId())
                .content("您有一个新的班级活动：" + activity.getTitle() + "，请按时参加！")
                .isRead(0)
                .createTime(activity.getCreateTime())
                .build()).toList();
        notificationMapper.insertNotifications(notifications);
        // 推送通知
        studentIds.forEach(id -> {
            try {
                userWebSocketServer.sendToOne(id.toString(), "您有一个新的班级活动通知，请及时查看！");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        log.info("活动创建成功: {}", activity.getId());
    }
}
