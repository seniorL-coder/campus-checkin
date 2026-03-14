package com.wangwei.service;

import com.wangwei.dto.ActivityDTO;

public interface ActivityService {

    /**
     * 创建活动
     * @param activityDTO 活动DTO对象
     */
    void createActivity(ActivityDTO activityDTO);
}
