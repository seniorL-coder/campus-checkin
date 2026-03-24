package com.wangwei.service;

import com.wangwei.dto.LoginDTO;
import com.wangwei.vo.LoginVO;
import com.wangwei.vo.UserVO;

import java.util.List;

public interface UserService {
    LoginVO login(LoginDTO loginDTO);

    void logout(Integer userId);

    /**
     * 根据用户ID获取用户信息
     * @return 用户信息
     */
    UserVO info();

    /**
     * 根据班级ID获取学生列表
     * @param classIds 班级ID 列表，用逗号分隔
     * @return 学生列表
     */
    List<UserVO> getStudentsByClassIds(List<Integer> classIds);

    Long getTotalStudentCount();

    Long getTodayNewStudentCount();
}
