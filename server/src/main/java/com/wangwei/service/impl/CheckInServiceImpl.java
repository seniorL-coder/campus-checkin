package com.wangwei.service.impl;


import com.wangwei.context.BaseContext;
import com.wangwei.dto.CheckInDTO;
import com.wangwei.entity.Activity;
import com.wangwei.entity.CheckIn;
import com.wangwei.exception.AlreadyCheckedInException;
import com.wangwei.exception.CheckInRecordNotFoundException;
import com.wangwei.exception.CheckInTimeOutOfRangeException;
import com.wangwei.exception.DistanceOverTargetRadiusException;
import com.wangwei.mapper.CheckInMapper;
import com.wangwei.service.ActivityService;
import com.wangwei.service.CheckInService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.wangwei.utils.GeoUtils;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckInServiceImpl implements CheckInService {
    private final CheckInMapper checkInMapper;
    private final ActivityService activityService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkIn(CheckInDTO checkInDTO) {
        // 1.  根据 传入的 userId 和 activityId 查询签到信息
        Long userId = BaseContext.getCurrentId();

        log.info("userId: {}", userId);
        CheckIn record = getRecordByUserIdAndActivityId(userId, checkInDTO.getActivityId());
        if (record == null) {
            throw new CheckInRecordNotFoundException("签到记录不存在");
        }
        if (record.getStatus() == 1) {
            throw new AlreadyCheckedInException("已签到");
        }
        // 2. 如果t_check_in 活动记录存在, 通过 activityId 查询活动信息
        Activity activity = activityService.getActivityById(checkInDTO.getActivityId());
        if (activity == null) {
            throw new CheckInRecordNotFoundException("活动不存在");
        }
        // 2.1 拿到活动详情 开始时间, 结束时间, 经纬度
        String startTime = activity.getStartTime();
        String endTime = activity.getEndTime();
        Double longitude = activity.getLongitude();
        Double latitude = activity.getLatitude();
        // 2.2 拿到活动详情 半径
        Double radius = activity.getRadius();
        // 3. 判断签到时间是否在活动开始时间和结束时间之间
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (!now.isAfter(LocalDateTime.parse(startTime, formatter)) || !now.isBefore(LocalDateTime.parse(endTime, formatter))) {
            throw new CheckInTimeOutOfRangeException("签到时间不在活动时间内");
        }
        // 4. 判断签到地点是否在活动地点附近
        double distance = GeoUtils.distance(checkInDTO.getLon(), checkInDTO.getLat(), longitude, latitude);
        if (distance > radius) {
            CheckIn checkIn = CheckIn.builder()
                    .userId(userId)
                    .activityId(checkInDTO.getActivityId())
                    .checkTime(LocalDateTime.now().toString())
                    .status(3).build();
            checkInMapper.updateCheckInStatus(checkIn); // 3: 签到距离超过目标范围
            throw new DistanceOverTargetRadiusException("签到距离超过目标范围");
        }
        // 5. 如果满足条件, 更新 t_check_in 表中的签到状态为已签到
        CheckIn checkIn = CheckIn.builder()
                .userId(userId)
                .activityId(checkInDTO.getActivityId())
                .checkTime(LocalDateTime.now().toString())
                .status(1).build();
        checkInMapper.updateCheckInStatus(checkIn); // 1: 已签到
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
}
