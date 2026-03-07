package com.wangwei.service;

import com.wangwei.dto.UserLoginDTO;
import com.wangwei.vo.UserVO;

public interface UserService {
    UserVO login(UserLoginDTO userLoginDTO);
}
