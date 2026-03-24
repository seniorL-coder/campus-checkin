package com.wangwei.mapper;

import com.wangwei.dto.CheckInQueryDTO;
import com.wangwei.entity.CheckIn;
import com.wangwei.vo.AttendanceVO;
import com.wangwei.vo.CheckInVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface CheckInMapper {
    /**
     * 插入签到记录
     *
     * @param checkIns 签到记录 List<CheckIn>
     */
    void insertCheckIns(List<CheckIn> checkIns);

    /**
     * 根据用户ID和活动ID获取签到记录
     *
     * @param userId     用户ID
     * @param activityId 活动ID
     */
    CheckIn getRecordByUserIdAndActivityId(Long userId, Long activityId);


    /**
     * 更新签到状态
     *
     * @param checkIn 签到记录
     */
    void updateCheckInStatus(CheckIn checkIn);

    /**
     * 更新签到状态（活动结束）
     *
     * @param activityId 活动ID
     */
    void updateCheckInStatusForFinishedActivity(Long activityId);


    /**
     * 今日签到日程
     */
    List<CheckInVO> selectTodaySchedule(Long userId);

    /**
     * 查询签到记录
     */
    List<CheckInVO> selectCheckInRecords(Long userId,
                                         Integer status);

    AttendanceVO selectAttendance(Long userId);

    /**
     * 同步更新过期活动的签到记录状态
     */
    void updateAbsentStatusByFinishedActivities();


    /**
     * 根据条件查询签到记录
     * @param checkInQueryDTO 签到记录查询条件
     * @return 签到记录列表
     */
    List<CheckInVO> list(CheckInQueryDTO checkInQueryDTO);
}
