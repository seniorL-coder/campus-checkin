package com.wangwei.service;

import com.wangwei.dto.LoginDTO;
import com.wangwei.entity.User;
import com.wangwei.vo.LoginVO;
import com.wangwei.vo.UserVO;

import java.util.List;

public interface UserService {
    LoginVO login(LoginDTO loginDTO);

    void logout(Integer userId);

    /**
     * 根据用户ID获取用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    UserVO info(Integer userId);

    /**
     * 根据班级ID获取学生列表
      * @param classId 班级ID
     * @return 学生列表
     */
    List<UserVO> getStudentsByClassId(Integer classId);

}
