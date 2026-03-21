package com.wangwei.service.impl;


import com.wangwei.context.BaseContext;
import com.wangwei.dto.CheckInDTO;
import com.wangwei.entity.Activity;
import com.wangwei.entity.CheckIn;
import com.wangwei.exception.*;
import com.wangwei.mapper.CheckInMapper;
import com.wangwei.result.Result;
import com.wangwei.service.ActivityService;
import com.wangwei.service.CheckInService;
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
import java.util.List;

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
            throw new QrCodeExpiredException("二维码已失效，请刷新后重试");
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
        // 4. 判断签到地点是否在活动地点附近
        double distance = GeoUtils.distance(checkInDTO.getLon(), checkInDTO.getLat(), longitude, latitude);
        if (distance > radius) {
            CheckIn checkIn = CheckIn.builder()
                    .userId(userId)
                    .activityId(activityId)
                    .checkTime(LocalDateTime.now().toString())
                    .status(3).build();
            checkInMapper.updateCheckInStatus(checkIn); // 3: 签到距离超过目标范围
            throw new DistanceOverTargetRadiusException("签到距离超过目标范围, 距离边界" + radius + "米");
        }
        // 5. 如果满足条件, 更新 t_check_in 表中的签到状态为已签到
        CheckIn checkIn = CheckIn.builder()
                .userId(userId)
                .activityId(activityId)
                .checkTime(LocalDateTime.now().toString())
                .status(1).build();
        checkInMapper.updateCheckInStatus(checkIn); // 1: 已签到
        try {
            adminWebSocketServer.sendToOne(String.valueOf(activity.getCreateUserId()), "学生 " + userId + " 已签到");
        } catch (Exception e) {
            log.error("发送签到通知失败", e);
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
                item.setStatus(0);
            } else if (now.isAfter(item.getEndTime()) && item.getCheckTime() == null) {
                item.setStatus(2);
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
                item.setStatus(1);
                continue;
            }

            // 未开始
            if (now.isBefore(item.getStartTime())) {
                item.setStatus(0);
            }
            // 已结束但未签到 -> 缺勤
            else if (now.isAfter(item.getEndTime())) {
                item.setStatus(2);
            }
            // 进行中（你可以自定义，比如仍算未开始 or 新状态）
            else {
                item.setStatus(0);
            }
        }

        return list;
    }

}
