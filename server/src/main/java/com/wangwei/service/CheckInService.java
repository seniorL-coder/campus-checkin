package com.wangwei.service;

import com.wangwei.dto.CheckInDTO;
import com.wangwei.entity.CheckIn;

public interface CheckInService {
    /**
     * 签到
     * @param checkInDTO 签到信息
     */
    void checkIn(CheckInDTO checkInDTO);

    /**
     * 根据用户ID和活动ID获取签到记录
     * @param userId 用户ID
     * @param activityId 活动ID
     */
    CheckIn getRecordByUserIdAndActivityId(Long userId, Long activityId);
}
