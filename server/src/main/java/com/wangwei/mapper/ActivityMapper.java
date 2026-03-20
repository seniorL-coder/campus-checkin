package com.wangwei.mapper;

import com.github.pagehelper.Page;
import com.wangwei.annotation.AutoFill;
import com.wangwei.dto.ActivityQueryDTO;
import com.wangwei.entity.Activity;
import com.wangwei.entity.CheckIn;
import com.wangwei.entity.Class;
import com.wangwei.entity.Notification;
import com.wangwei.enumeration.OperationType;
import com.wangwei.vo.ActivityVO;
import com.wangwei.vo.ClassVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ActivityMapper {

    /**
     * 插入一条活动记录
     *
     * @param activity 活动记录
     */
    @AutoFill(value = OperationType.INSERT)
    void insert(Activity activity);

    /**
     * 根据id获取活动记录
     *
     * @param activityId 活动记录id
     * @return 活动记录
     */
    @Select("SELECT id, title, start_time, end_time, longitude, latitude, radius, target_classes, create_user_id, create_time, update_time  FROM t_activity WHERE id = #{activityId}")
    Activity getActivityById(Long activityId);

    /**
     * 根据条件获取活动记录列表
     * @param activityQueryDTO
     * @return
     */
    Page<ActivityVO> list(ActivityQueryDTO activityQueryDTO);

    /**
     * 批量更新活动记录状态
     * @param updatedList
     */
    void updateActivityBatch(List<Activity> updatedList);

    /**
     * 根据id更新活动记录
     * @param activity 活动记录
     */
    void updateActivityById(Activity activity);
}
