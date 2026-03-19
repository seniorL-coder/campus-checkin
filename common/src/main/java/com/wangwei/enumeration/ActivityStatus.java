package com.wangwei.enumeration;

public enum ActivityStatus {
    NOT_STARTED(0, "未开始"),
    ONGOING(1, "进行中"),
    FINISHED(2, "已结束");
    // 1. 构造函数：初始化数据
    private final int code;
    private final String desc;
    ActivityStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    // 2. Getter：让外部能拿到 code 或 desc
    public int getCode() { return code; }
    public String getDesc() { return desc; }
}
