package com.wangwei.service;

import com.wangwei.dto.CheckInDTO;
import com.wangwei.dto.CheckInQueryDTO;
import com.wangwei.entity.CheckIn;
import com.wangwei.result.PageResult;
import com.wangwei.vo.AttendanceVO;
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

    /**
     * 获取出勤率统计
     * @return
     */
    AttendanceVO getAttendance();

    /**
     * 分页获取签到记录
     * @param checkInQueryDTO 签到查询条件
     * @return 签到记录分页结果
     */
    PageResult<CheckInVO> list(CheckInQueryDTO checkInQueryDTO);

    /**
     * 修改学生签到状态
     * @param activityId 活动ID
     * @param status 签到状态 0-未签到 1-已签到 2-缺勤 3-范围外签到
     */
    void updateCheckInStatus(Long userId, Long activityId, Integer status);
}
