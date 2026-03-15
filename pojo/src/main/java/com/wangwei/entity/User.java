package com.wangwei.entity;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
@Getter
@Setter
@ToString
@Builder
public class User implements Serializable {

    @Schema(description = "用户ID")
    private Long id;
    @Schema(description = "登录账号(学号/工号)")
    private String username;
    @Schema(description = "登录密码")
    private String password;
    @Schema(description = "真实姓名")
    private String real_name;
    @Schema(description = "角色: 0-学生, 1-管理员")
    private int role;
    @Schema(description = "所属班级ID( 学生必填, 管理员为空)")
    private Integer class_id;
    @Schema(description = "创建时间")
    private LocalDateTime create_time;
    @Schema(description = "更新时间")
    private LocalDateTime update_time;
}
