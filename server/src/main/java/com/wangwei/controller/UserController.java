package com.wangwei.controller;

import com.wangwei.dto.UserLoginDTO;
import com.wangwei.result.Result;
import com.wangwei.service.UserService;
import com.wangwei.utils.JwtUtils;
import com.wangwei.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user/user")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户管理相关接口")
public class UserController {
    private final UserService userService;

    // 登录接口
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录接口")
    public Result<UserVO> login(@RequestBody UserLoginDTO userLoginDTO) {
        UserVO userVO = userService.login(userLoginDTO);
        return Result.success(userVO);
    }
}
