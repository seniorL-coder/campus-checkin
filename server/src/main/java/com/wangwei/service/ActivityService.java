package com.wangwei.service;

import com.wangwei.dto.ActivityDTO;
import com.wangwei.dto.ActivityQueryDTO;
import com.wangwei.dto.SignDTO;
import com.wangwei.entity.Activity;
import com.wangwei.result.PageResult;
import com.wangwei.vo.ActivityVO;

import java.util.List;

public interface ActivityService {

    /**
     * 创建活动
     * @param activityDTO 活动DTO对象
     */
    void createActivity(ActivityDTO activityDTO);

    /**
     * 根据id获取活动
     * @param activityId
     * @return
     */
    Activity getActivityById(Long activityId);

    /**
     * 创建活动签到的url跳转链接
     * @param signDTO
     * @return
     */
    String createActivitySign(SignDTO signDTO);

    /**
     * 分页查询活动列表
     * @param activityQueryDTO
     * @return
     */
    PageResult<ActivityVO> list(ActivityQueryDTO activityQueryDTO);
}
