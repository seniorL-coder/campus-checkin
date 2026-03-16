package com.wangwei.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wangwei.context.BaseContext;
import com.wangwei.dto.ActivityDTO;
import com.wangwei.dto.ActivityQueryDTO;
import com.wangwei.dto.SignDTO;
import com.wangwei.entity.Activity;
import com.wangwei.entity.CheckIn;
import com.wangwei.entity.Notification;
import com.wangwei.mapper.ActivityMapper;
import com.wangwei.mapper.CheckInMapper;
import com.wangwei.mapper.ClassMapper;
import com.wangwei.mapper.NotificationMapper;
import com.wangwei.result.PageResult;
import com.wangwei.service.ActivityService;
import com.wangwei.service.UserService;
import com.wangwei.vo.ActivityVO;
import com.wangwei.vo.ClassVO;
import com.wangwei.vo.UserVO;
import com.wangwei.websocket.UserWebSocketServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

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

    @Override
    public Activity getActivityById(Long activityId) {
        return activityMapper.getActivityById(activityId);
    }

    @Override
    public String createActivitySign(SignDTO signDTO) {
        log.info("开始生成动态签到链接, 活动ID: {}", signDTO.getActivityId());

        // 1. 生成随机 Token
        String token = UUID.randomUUID().toString(); // 使用 UUID.randomUUID()

        // 2. 存入 Redis，设置 60 秒过期 (比前端刷新频率稍微长一点点，防止卡顿失效)
        String redisKey = SIGN_TOKEN_PREFIX + signDTO.getActivityId();
        redisTemplate.opsForValue().set(redisKey, token, 120, TimeUnit.SECONDS);

        // 3. 构建带参数的跳转 URL
        // 这里的 baseUrl 应该是你 H5 签到页面的地址
        String baseUrl = "/campus-check-in";
        return String.format("%s?activityId=%s&token=%s",
                baseUrl, signDTO.getActivityId(), token);
    }

    @Override
    public PageResult<ActivityVO> list(ActivityQueryDTO activityQueryDTO) {
        activityQueryDTO.setCreateUserId(BaseContext.getCurrentId());
        // 1. 开启分页
        PageHelper.startPage(activityQueryDTO.getPage(), activityQueryDTO.getLimit());

        // 2. 执行查询
        List<ActivityVO> list = activityMapper.list(activityQueryDTO);

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
}
