package com.wangwei.service.impl;

import com.wangwei.context.BaseContext;
import com.wangwei.mapper.NotificationMapper;
import com.wangwei.service.NotificationService;
import com.wangwei.vo.NotificationVO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper;

    public NotificationServiceImpl(NotificationMapper notificationMapper) {
        this.notificationMapper = notificationMapper;
    }

    @Override
    public Integer getUnreadCount() {
        Long userId = BaseContext.getCurrentId();
        return notificationMapper.countUnread(userId);
    }

    @Override
    public List<NotificationVO> getList() {
        Long userId = BaseContext.getCurrentId();
        return notificationMapper.selectList(userId);
    }

    @Override
    public void readAll() {
        Long userId = BaseContext.getCurrentId();
        notificationMapper.updateAllRead(userId, LocalDateTime.now());
    }

    @Override
    public void readOne(Long id) {
        notificationMapper.updateOneRead(id, LocalDateTime.now());
    }
}