package com.wangwei.controller.admin;

import com.wangwei.dto.LogoutDTO;
import com.wangwei.dto.LoginDTO;
import com.wangwei.result.Result;
import com.wangwei.service.UserService;
import com.wangwei.vo.LoginVO;
import com.wangwei.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/user")
@RequiredArgsConstructor
@Tag(name = "用户管理")
@Slf4j
public class UserController {
    private final UserService userService;

    // 登录接口
    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public Result<LoginVO> login(@RequestBody LoginDTO loginDTO) {
        LoginVO loginVO = userService.login(loginDTO);
        return Result.success(loginVO);
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出")
    public Result<?> logout(@RequestBody LogoutDTO logoutDTO) {
        log.info("userId退出登录: {}", logoutDTO.getUserId());
        Integer userId = logoutDTO.getUserId();
        userService.logout(userId);
        return Result.success("退出登录成功");
    }
    @PostMapping("/info")
    @Operation(summary = "获取用户信息")
    public Result<UserVO> info() {
        UserVO userVO = userService.info();
        return Result.success(userVO);
    }
}
