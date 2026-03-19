package com.wangwei.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户VO")
public class UserVO implements Serializable {
    @Schema(description = "用户ID")
    private Long id;
    @Schema(description = "登录账号(学号/工号)")
    private String username;
    @Schema(description = "真实姓名")
    private String realName;
    @Schema(description = "用户角色 0:学生 1:老师")
    private Integer role;
    @Schema(description = "头像")
    private String avatar;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}
