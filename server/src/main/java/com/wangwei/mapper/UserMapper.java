package com.wangwei.mapper;

import com.wangwei.dto.LoginDTO;
import com.wangwei.vo.LoginVO;
import com.wangwei.vo.UserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    // 用户登录
    LoginVO login(LoginDTO loginDTO);

    @Select("SELECT  id, username, password, real_name, role, class_id, create_time, update_date, avatar FROM t_user WHERE id = ${userId}")
    UserVO getUserInfoById(Integer userId);
}
