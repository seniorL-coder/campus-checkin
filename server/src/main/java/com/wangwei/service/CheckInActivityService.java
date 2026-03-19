package com.wangwei.service;

import java.util.List;

public interface CheckInActivityService {
    void updateCheckInStatusForFinishedActivity(List<Long> activityIds);
}
