package com.wangwei.exception;

/**
 * 签到时间不在活动时间范围内异常
 */
public class CheckInTimeOutOfRangeException extends BaseException {

    public CheckInTimeOutOfRangeException(String msg) {
        super(msg);
    }

}