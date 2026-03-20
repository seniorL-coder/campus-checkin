package com.wangwei.service.impl;

import com.fasterxml.jackson.databind.util.BeanUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wangwei.context.BaseContext;
import com.wangwei.dto.ActivityDTO;
import com.wangwei.dto.ActivityQueryDTO;
import com.wangwei.dto.SignDTO;
import com.wangwei.dto.UpdateActivityDTO;
import com.wangwei.entity.Activity;
import com.wangwei.entity.CheckIn;
import com.wangwei.entity.Notification;
import com.wangwei.enumeration.ActivityStatus;
import com.wangwei.enumeration.CheckInStatus;
import com.wangwei.exception.*;
import com.wangwei.exception.IllegalArgumentException;
import com.wangwei.mapper.ActivityMapper;
import com.wangwei.mapper.CheckInMapper;
import com.wangwei.mapper.ClassMapper;
import com.wangwei.mapper.NotificationMapper;
import com.wangwei.result.PageResult;
import com.wangwei.service.ActivityService;
import com.wangwei.service.CheckInActivityService;
import com.wangwei.service.CheckInService;
import com.wangwei.service.UserService;
import com.wangwei.vo.ActivityVO;
import com.wangwei.vo.ClassVO;
import com.wangwei.vo.UserVO;
import com.wangwei.websocket.UserWebSocketServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.swing.text.DateFormatter;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
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
    private final RedisTemplate<String, String> redisTemplate;

    private static final String SIGN_TOKEN_PREFIX = "sign_token:";
    private final ClassMapper classMapper;
    private final CheckInActivityService checkInActivityService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createActivity(ActivityDTO activityDTO) {
        log.info("创建活动: {}", activityDTO);
        // First: 判断活动结束时间是否小于当前时间, 如果是则抛出异常
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime endTime = activityDTO.getEndTime();
        log.info("活动结束时间: {}", endTime);
        log.info("当前时间: {}", LocalDateTime.now());
        if (endTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("活动结束时间不能小于当前时间");
        }
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
                .status(ActivityStatus.NOT_STARTED.getCode()) // 默认状态为 0 -> 未开始，使用枚举获取状态码
                .build();
        activityMapper.insert(activity);
        log.info("活动创建成功: {}", activity.getId());
    }

    @Override
    public Activity getActivityById(Long activityId) {
        return activityMapper.getActivityById(activityId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createActivitySign(SignDTO signDTO) {
        log.info("开始生成动态签到链接, 活动ID: {}", signDTO.getActivityId());
        // 查询当前活动 在不在 签到时间内
        Activity activity = activityMapper.getActivityById(signDTO.getActivityId());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (activity == null) throw new CheckInRecordNotFoundException("活动不存在");
        LocalDateTime startTime = activity.getStartTime();
        LocalDateTime endTime = activity.getEndTime();
        if (LocalDateTime.now().isBefore(startTime) || LocalDateTime.now().isAfter(endTime)) {
            throw new ActivityCheckInNotBetweenTimeException("当前时间不在签到时间内");
        }
        // 1. 生成随机 Token
        String token = UUID.randomUUID().toString(); // 使用 UUID.randomUUID()

        // 2. 存入 Redis，设置 60 秒过期 (比前端刷新频率稍微长一点点，防止卡顿失效)
        String redisKey = SIGN_TOKEN_PREFIX + signDTO.getActivityId();
        redisTemplate.opsForValue().set(redisKey, token, 120, TimeUnit.SECONDS);

        // 3. 查询班级id下的所有学生, 添加到 签到流水中 和 消息通知表中, 注意事务一致性
        //3.1 查询学生列表
        List<Integer> targetClassesIds = Stream.of(activity.getTargetClasses().split(",")).map(Integer::parseInt).toList();
        List<UserVO> students = userService.getStudentsByClassIds(targetClassesIds);
        List<Long> studentIds = students.stream().map(UserVO::getId).toList();
        //3.2 添加到签到流水中
        // 3.2.1 构建签到数据 List,
        List<CheckIn> checkIns = studentIds.stream().map(id -> CheckIn.builder()
                .userId(id)
                .activityId(activity.getId())
                .status(CheckInStatus.PENDING.getCode())
                .build()).toList();
        // 3.2.2 批量插入签到数据
        checkInMapper.insertCheckIns(checkIns);
        // 3.3 添加到消息通知表中
        // 3.3.1 构建消息通知数据 List,
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

        // 4. 构建带参数的跳转 URL
        // 这里的 baseUrl 应该是你 H5 签到页面的地址
        // String baseUrl = "/campus-check-in";
        return String.format("?activityId=%s&token=%s", signDTO.getActivityId(), token);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PageResult<ActivityVO> list(ActivityQueryDTO activityQueryDTO) {
        activityQueryDTO.setCreateUserId(BaseContext.getCurrentId());
        // 1. 开启分页
        PageHelper.startPage(activityQueryDTO.getPage(), activityQueryDTO.getLimit());

        // 2. 执行查询
        List<ActivityVO> list = activityMapper.list(activityQueryDTO);
        log.info("活动列表-----------: {}", list);
        // 懒更新活动状态, 判断返回的活动列表的结束时间与当前时间的状态
        // 1, 结束时间小于当前时间, 则状态为 2 -> 已结束
        //2. 结束时间大于当前时间, 则状态为 1 -> 进行中
        List<Activity> updatedList = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        list.forEach(item -> {
            if (item.getStatus() == ActivityStatus.FINISHED.getCode()) return;
            int oldStatus = item.getStatus();
            Activity activity = new Activity();
            if (now.isBefore(item.getStartTime())) {
                // 未开始
                item.setStatus(ActivityStatus.NOT_STARTED.getCode());
            } else if (now.isAfter(item.getEndTime())) {
                // 已结束
                item.setStatus(ActivityStatus.FINISHED.getCode());
            } else {
                // 进行中
                item.setStatus(ActivityStatus.ONGOING.getCode());
            }
            BeanUtils.copyProperties(item, activity);

            if (oldStatus != item.getStatus()) {
                updatedList.add(activity);
            }
        });
        if (!updatedList.isEmpty()) {
            // 批量更新活动状态，如果更新列表不为空，则进行更新操作
            activityMapper.updateActivityBatch(updatedList);
            // 这里还要更新t_check_in签到流水表中对应活动的状态, 如果活动状态为已完成, 对应签到流水表中的学生签到状态若果为0(未开始)
            // 则需要将对应签到流水表中的学生签到状态更新为2(缺勤)
            // 调用 StudentService 中的方法来更新签到状态
            // 活动id的List
            List<Long> activityIds = updatedList.stream().map(Activity::getId).toList();
            log.info("更新签到状态, 活动ID: {}", activityIds);
            checkInActivityService.updateCheckInStatusForFinishedActivity(activityIds);
        }

        // 3. 使用 PageInfo 获取元数据（比直接强转 Page 更稳妥）
        PageInfo<ActivityVO> pageInfo = new PageInfo<>(list);

        List<ActivityVO> activityList = pageInfo.getList();

        if (CollectionUtils.isEmpty(activityList)) {
            return new PageResult<>(pageInfo.getPageNum(), pageInfo.getPages(), pageInfo.getPageSize(), pageInfo.getTotal(), activityList);
        }

        // 2. 收集所有涉及到的 Class ID
        Set<Long> allClassIds = activityList.stream()
                .filter(vo -> StringUtils.hasText(vo.getTargetClasses()))
                .flatMap(vo -> Arrays.stream(vo.getTargetClasses().split(",")))
                .map(String::trim)
                .map(Long::valueOf)
                .collect(Collectors.toSet());

        // 3. 批量查询班级信息 (select id, name from t_class where id in (...))
        List<ClassVO> clazzes = classMapper.selectBatchIds(allClassIds);
        Map<Long, String> classMap = clazzes.stream()
                .collect(Collectors.toMap(ClassVO::getId, ClassVO::getClassName));

        // 4. 回填班级名称
        activityList.forEach(vo -> {
            if (StringUtils.hasText(vo.getTargetClasses())) {
                String names = Arrays.stream(vo.getTargetClasses().split(","))
                        .map(id -> classMap.getOrDefault(Long.valueOf(id.trim()), "未知班级"))
                        .collect(Collectors.joining(","));
                vo.setTargetClassesName(names);
            }
        });

        return new PageResult<>(
                pageInfo.getPageNum(),
                pageInfo.getPages(),
                pageInfo.getPageSize(),
                pageInfo.getTotal(),
                pageInfo.getList()
        );
    }

    /**
     * 更新活动状态为已完成
     *
     * @param id 活动ID
     */
    @Override
    public void updateActivityStatusToFinished(Long id) {
        // 这里要做多方面更新
        // 1. 更新t_check_in 签到流水表中对应活动的未签到的签到记录为缺勤
        checkInMapper.updateCheckInStatusForFinishedActivity(id);
        // 2. 更新t_activity 活动表中对应活动的状态为已完成
        activityMapper.updateActivityBatch(List.of(Activity.builder().id(id).status(ActivityStatus.FINISHED.getCode()).build()));
    }

    /**
     * 更新活动信息
     *
     * @param updateActivityDTO 更新活动DTO对象
     */
    @Override
    public void updateActivity(UpdateActivityDTO updateActivityDTO) {
        LocalDateTime startTime = updateActivityDTO.getStartTime();
        LocalDateTime endTime = updateActivityDTO.getEndTime();
        if (
                (updateActivityDTO.getTitle() == null || updateActivityDTO.getTitle().trim().isEmpty())
                        && startTime == null
                        && endTime == null
        ) {
            throw new IllegalArgumentException("请至少更新一个字段");
        }

        if (startTime != null && endTime != null && startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("开始时间不能晚于结束时间");
        }
        Activity activity = new Activity();
        BeanUtils.copyProperties(updateActivityDTO, activity);
        activityMapper.updateActivityById(activity);
    }
}
