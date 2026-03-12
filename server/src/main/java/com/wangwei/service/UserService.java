package com.wangwei.service;

import com.wangwei.dto.LoginDTO;
import com.wangwei.vo.LoginVO;
import com.wangwei.vo.UserVO;

public interface UserService {
    LoginVO login(LoginDTO loginDTO);

    void logout(Integer userId);

    UserVO info(Integer userId);
}
