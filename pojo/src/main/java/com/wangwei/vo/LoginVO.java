package com.wangwei.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "登录成功响应")
public class LoginVO {
    @Schema(description = "用户ID")
    private Integer id;
    @Schema(description = "用户名")
    private String username;
    @Schema(description = "真实姓名")
    private String realName;
    @Schema(description = "令牌")
    private String token;
    @Schema(description = "角色 0 学生 1 老师")
    private Integer role; // 0 学生 1 老师
}
