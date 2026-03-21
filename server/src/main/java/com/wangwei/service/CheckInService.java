package com.wangwei.service;

import com.wangwei.dto.CheckInDTO;
import com.wangwei.entity.CheckIn;
import com.wangwei.vo.CheckInVO;

import java.util.List;

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


    /**
     * 获取今日签到日程
     */
    List<CheckInVO> getTodaySchedule();

    /**
     * 获取签到记录（可按状态筛选）
     */
    List<CheckInVO> getCheckInRecords(Integer status);
}
