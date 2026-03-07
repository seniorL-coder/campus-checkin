package com.wangwei.controller;

import com.wangwei.dto.LogoutDTO;
import com.wangwei.dto.LoginDTO;
import com.wangwei.result.Result;
import com.wangwei.service.UserService;
import com.wangwei.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/user")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户管理相关接口")
public class UserController {
    private final UserService userService;

    // 登录接口
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录接口")
    public Result<UserVO> login(@RequestBody LoginDTO loginDTO) {
        UserVO userVO = userService.login(loginDTO);
        return Result.success(userVO);
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "用户登出接口")
    public Result<?> logout(@RequestBody LogoutDTO logoutDTO) {
        Integer userId = logoutDTO.getUserId();
        userService.logout(userId);
        return Result.success();
    }
}
