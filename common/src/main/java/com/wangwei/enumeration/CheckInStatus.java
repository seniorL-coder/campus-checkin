package com.wangwei.enumeration;

import lombok.Getter;

@Getter
public enum CheckInStatus {
    PENDING(0, "待签到"),
    SUCCESS(1, "正常"),
    ABSENT(2, "缺勤"),
    // 范围外
    OUT_OF_RANGE(3, "范围外");

    private final int code;
    private final String desc;

    CheckInStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}