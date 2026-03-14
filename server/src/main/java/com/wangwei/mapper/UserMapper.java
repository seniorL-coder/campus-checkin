package com.wangwei.mapper;

import com.wangwei.dto.LoginDTO;
import com.wangwei.entity.User;
import com.wangwei.vo.LoginVO;
import com.wangwei.vo.UserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper {
    // 用户登录
    LoginVO login(LoginDTO loginDTO);

    /**
     * 根据用户ID获取用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    @Select("SELECT  id, username, password, real_name, role, class_id, create_time, update_time, avatar FROM t_user WHERE id = ${userId}")
    UserVO getUserInfoById(Integer userId);

    /**
     * 根据班级ID获取学生列表
     * @param classIds 班级ID 列表，用逗号分隔
     * @return 学生列表
     */
    List<UserVO> getStudentsByClassIds(List<Integer> classIds);
}
