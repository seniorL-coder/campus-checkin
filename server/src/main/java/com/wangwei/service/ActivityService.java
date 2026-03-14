package com.wangwei.service;

import com.wangwei.dto.ActivityDTO;
import com.wangwei.dto.SignDTO;
import com.wangwei.entity.Activity;

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
}
