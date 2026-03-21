package com.wangwei.service;

import com.wangwei.vo.NotificationVO;

import java.util.List;

public interface NotificationService {

    Integer getUnreadCount();

    List<NotificationVO> getList();

    void readAll();

    void readOne(Long id);

}
