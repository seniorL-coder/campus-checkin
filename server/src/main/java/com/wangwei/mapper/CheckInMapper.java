package com.wangwei.mapper;

import com.wangwei.entity.CheckIn;
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
    @Update("UPDATE t_check_in SET status = #{status}, check_time = #{checkTime} WHERE user_id = #{userId} AND activity_id = #{activityId}")
    void updateCheckInStatus(CheckIn checkIn);

    /**
     * 更新签到状态（活动结束）
     *
     * @param activityId 活动ID
     */
    void updateCheckInStatusForFinishedActivity(Long activityId);
}
