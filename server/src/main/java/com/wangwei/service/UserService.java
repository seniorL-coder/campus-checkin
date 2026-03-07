package com.wangwei.service;

import com.wangwei.dto.LoginDTO;
import com.wangwei.vo.UserVO;

public interface UserService {
    UserVO login(LoginDTO loginDTO);

    void logout(Integer userId);
}
