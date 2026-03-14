package com.wangwei.mapper;

import com.wangwei.entity.Notification;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NotificationMapper {

    /**
     * 插入消息通知记录
     * @param notifications 消息通知记录 List<Notification>
     */
    void insertNotifications(List<Notification> notifications);
}
