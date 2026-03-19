package com.wangwei.service.impl;

import com.wangwei.mapper.CheckInMapper;
import com.wangwei.service.CheckInActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckInActivityServiceImpl implements CheckInActivityService {

    private final CheckInMapper checkInMapper;

    /**
     * 更新活动状态为已完成的活动对应的学生签到状态为0(未开始)->2(缺勤)
     *
     * @param activityIds 活动ID列表
     */
    @Override
    public void updateCheckInStatusForFinishedActivity(List<Long> activityIds) {
        if (!activityIds.isEmpty()) {
            activityIds.forEach(checkInMapper::updateCheckInStatusForFinishedActivity);
        }
    }
}
