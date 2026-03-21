package com.wangwei.mapper;

import com.wangwei.entity.Notification;
import com.wangwei.vo.NotificationVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface NotificationMapper {

    /**
     * 插入消息通知记录
     *
     * @param notifications 消息通知记录 List<Notification>
     */
    void insertNotifications(List<Notification> notifications);

    /**
     * 查询未读消息通知记录数
     * @param userId
     * @return
     */
    Integer countUnread(Long userId);

    /**
     * 查询用户消息通知记录
     * @param userId
     * @return
     */
    List<NotificationVO> selectList(Long userId);

    /**
     * 更新所有消息通知为已读
     * @param userId
     * @param readTime
     */
    void updateAllRead(@Param("userId") Long userId,
                       @Param("readTime") LocalDateTime readTime);

    /**
     * 更新单条消息通知为已读
     * @param id
     * @param readTime
     */
    void updateOneRead(@Param("id") Long id,
                       @Param("readTime") LocalDateTime readTime);
}

