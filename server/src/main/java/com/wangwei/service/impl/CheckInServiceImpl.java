package com.wangwei.service.impl;


import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wangwei.context.BaseContext;
import com.wangwei.dto.CheckInDTO;
import com.wangwei.dto.CheckInQueryDTO;
import com.wangwei.entity.Activity;
import com.wangwei.entity.CheckIn;
import com.wangwei.enumeration.ActivityStatus;
import com.wangwei.enumeration.CheckInStatus;
import com.wangwei.exception.*;
import com.wangwei.mapper.CheckInMapper;
import com.wangwei.result.PageResult;
import com.wangwei.result.Result;
import com.wangwei.service.ActivityService;
import com.wangwei.service.CheckInService;
import com.wangwei.vo.AttendanceVO;
import com.wangwei.vo.CheckInVO;
import com.wangwei.websocket.AdminWebSocketServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.wangwei.utils.GeoUtils;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckInServiceImpl implements CheckInService {

    private static final String SIGN_TOKEN_PREFIX = "sign_token:";

    private final CheckInMapper checkInMapper;
    private final RedisTemplate<String, String> redisTemplate;
    private final ActivityService activityService;
    private final AdminWebSocketServer adminWebSocketServer;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkIn(CheckInDTO checkInDTO) {
        // 1.  根据 传入的 activityId 查询签到信息
        Long userId = BaseContext.getCurrentId();
        Long activityId = checkInDTO.getActivityId();

        // 2. 校验 Redis 中的 Token
        String redisKey = SIGN_TOKEN_PREFIX + activityId;
        String validToken = redisTemplate.opsForValue().get(redisKey);
        if (validToken == null) {
            throw new QrCodeExpiredException("二维码已失效，请重新扫码");
        }
        if (!validToken.equals(checkInDTO.getToken())) {
            throw new InvalidCheckInTokenException("签到凭证无效，请重新扫码");
        }

        CheckIn record = getRecordByUserIdAndActivityId(userId, activityId);
        if (record == null) {
            throw new CheckInRecordNotFoundException("签到记录不存在");
        }
        if (record.getStatus() == 1) {
            throw new AlreadyCheckedInException("已签到");
        }
        // 2. 如果t_check_in 活动记录存在, 通过 activityId 查询活动信息
        Activity activity = activityService.getActivityById(activityId);
        if (activity == null) {
            throw new CheckInRecordNotFoundException("活动不存在");
        }
        // 2.1 拿到活动详情 开始时间, 结束时间, 经纬度
        LocalDateTime startTime = activity.getStartTime();
        LocalDateTime endTime = activity.getEndTime();
        Double longitude = activity.getLongitude();
        Double latitude = activity.getLatitude();
        // 2.2 拿到活动详情 半径
        Double radius = activity.getRadius();
        // 3. 判断签到时间是否在活动开始时间和结束时间之间
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(startTime) || now.isAfter(endTime)) {
            throw new CheckInTimeOutOfRangeException("签到时间不在活动时间内");
        }
        if (record.getStatus() == 2) { // 已经缺勤, 禁止
            throw new AlreadyAbsentException("已经缺勤, 禁止签到");
        }
        // 4. 判断签到地点是否在活动地点附近
        double distance = GeoUtils.distance(checkInDTO.getLon(), checkInDTO.getLat(), longitude, latitude);
        if (distance > radius) {
            CheckIn checkIn = CheckIn.builder()
                    .userId(userId)
                    .activityId(activityId)
                    .checkTime(LocalDateTime.now())
                    .status(3).build();
            checkInMapper.updateCheckInStatus(checkIn); // 3: 签到距离超过目标范围
            throw new DistanceOverTargetRadiusException("签到距离超过目标范围, 距离边界" + radius + "米");
        }
        // 5. 如果满足条件, 更新 t_check_in 表中的签到状态为已签到
        CheckIn checkIn = CheckIn.builder()
                .userId(userId)
                .activityId(activityId)
                .checkTime(LocalDateTime.now())
                .status(1).build();
        checkInMapper.updateCheckInStatus(checkIn); // 1: 已签到
        try {
            // 1. 构造推送给老师的数据
            Map<String, Object> msg = new HashMap<>();
            msg.put("type", "STUDENT_SIGNED_IN"); // 定义一个特定的类型

            Map<String, Object> data = new HashMap<>();
            data.put("userId", userId);
            data.put("activityId", activity.getId());
            data.put("checkTime", LocalDateTime.now().toString());
            // data.put("userName", userName);

            msg.put("data", data);

            // 2. 推送给创建该活动的老师 (注意：确保 sid 与登录时存入 SESSION_MAP 的 Key 一致)
            String teacherId = String.valueOf(activity.getCreateUserId());
            adminWebSocketServer.sendJson(teacherId, msg);

            log.info("已向老师 {} 推送学生 {} 的签到成功通知", teacherId, userId);

        } catch (Exception e) {
            // 签到通知失败不应影响学生签到的主业务逻辑，记录日志即可
            log.error("发送签到实时通知失败，studentId: {}, activityId: {}", userId, activity.getId(), e);
        }
    }

    /**
     * 根据用户ID和活动ID查询签到信息
     *
     * @param userId     用户ID
     * @param activityId 活动ID
     * @return
     */
    @Override
    public CheckIn getRecordByUserIdAndActivityId(Long userId, Long activityId) {
        return checkInMapper.getRecordByUserIdAndActivityId(userId, activityId);
    }


    /**
     * 今日签到日程
     */
    @Override
    public List<CheckInVO> getTodaySchedule() {
        Long userId = BaseContext.getCurrentId();
        List<CheckInVO> checkInVOList = checkInMapper.selectTodaySchedule(userId);
        LocalDateTime now = LocalDateTime.now();
        checkInVOList.forEach(item -> {
            if (now.isBefore(item.getStartTime())) {
                item.setStatus(CheckInStatus.PENDING.getCode());
            } else if ((now.isAfter(item.getEndTime()) && item.getCheckTime() == null)) {
                item.setStatus(CheckInStatus.ABSENT.getCode());
            } else if (now.isBefore(item.getEndTime()) && item.getCheckTime() == null && item.getActivityStatus() == ActivityStatus.FINISHED.getCode()) {
                item.setStatus(CheckInStatus.ABSENT.getCode());
            }
        });
        return checkInVOList;
    }

    /**
     * 获取全部签到记录
     *
     * @param status
     * @return
     */
    @Override
    public List<CheckInVO> getCheckInRecords(Integer status) {
        Long userId = BaseContext.getCurrentId();

        List<CheckInVO> list = checkInMapper.selectCheckInRecords(userId, status);

        LocalDateTime now = LocalDateTime.now();

        for (CheckInVO item : list) {

            // 已签到优先
            if (item.getCheckTime() != null) {
                item.setStatus(CheckInStatus.SUCCESS.getCode());
                continue;
            }

            // 未开始
            if (now.isBefore(item.getStartTime()) && item.getActivityStatus() == ActivityStatus.NOT_STARTED.getCode()) {
                item.setStatus(CheckInStatus.PENDING.getCode());
            }
            // 已结束但未签到 -> 缺勤
            else if (now.isAfter(item.getEndTime()) || item.getActivityStatus() == ActivityStatus.FINISHED.getCode()) {
                item.setStatus(CheckInStatus.ABSENT.getCode());
            }
            // 进行中（你可以自定义，比如仍算未开始 or 新状态）
            else {
                item.setStatus(CheckInStatus.PENDING.getCode());
            }
        }

        return list;
    }

    @Override
    public AttendanceVO getAttendance() {
        Long userId = BaseContext.getCurrentId();

        AttendanceVO vo = checkInMapper.selectAttendance(userId);

        int total = vo.getTotalCount() == null ? 0 : vo.getTotalCount();
        int checkIn = vo.getCheckInCount() == null ? 0 : vo.getCheckInCount();

        double rate = 0.0;

        if (total != 0) {
            rate = (checkIn * 100.0) / total;
        }

        vo.setAttendanceRate(Math.round(rate * 100.0) / 100.0); // 保留2位小数

        return vo;
    }

    @Override
    public PageResult<CheckInVO> list(CheckInQueryDTO checkInQueryDTO) {
        Integer page = checkInQueryDTO.getPage();
        Integer pageSize = checkInQueryDTO.getLimit();
        PageHelper.startPage(page, pageSize);
        List<CheckInVO> list = checkInMapper.list(checkInQueryDTO);
        Page<CheckInVO> pageInfo = (Page<CheckInVO>) list;
        List<CheckInVO> result = pageInfo.getResult();
        // 判断活动时间 (1, 活动时间过期 将活动状态更新为已结束)
        for (CheckInVO item : result) {
            if (item.getEndTime().isBefore(LocalDateTime.now()) && item.getCheckTime() == null) {
                item.setActivityStatus(ActivityStatus.FINISHED.getCode());
                item.setStatus(CheckInStatus.ABSENT.getCode());
                activityService.updateActivityStatusToFinished(item.getActivityId());
            }
        }


        return new PageResult<>(pageInfo.getPageNum(), pageInfo.getPages(), pageInfo.getPageSize(), pageInfo.getTotal(), result);
    }

    /**
     * 更新签到状态
     * @param userId 用户ID
     * @param activityId 活动ID
     * @param status 签到状态 0-未签到 1-已签到 2-缺勤 3-范围外签到
     */
    @Override
    public void updateCheckInStatus(Long userId, Long activityId, Integer status) {
        CheckIn checkIn = CheckIn.builder().userId(userId).activityId(activityId).status(status).checkTime(LocalDateTime.now()).build();
        checkInMapper.updateCheckInStatus(checkIn);
    }

}
