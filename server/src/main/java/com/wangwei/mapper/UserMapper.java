package com.wangwei.mapper;

import com.wangwei.dto.UserLoginDTO;
import com.wangwei.vo.UserVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    // 用户登录
    UserVO login(UserLoginDTO userLoginDTO);
}
