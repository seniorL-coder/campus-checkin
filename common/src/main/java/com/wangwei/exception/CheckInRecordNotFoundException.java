package com.wangwei.exception;

/**
 * 签到记录不存在异常
 */
public class CheckInRecordNotFoundException extends BaseException {

    public CheckInRecordNotFoundException(String msg) {
        super(msg);
    }

}