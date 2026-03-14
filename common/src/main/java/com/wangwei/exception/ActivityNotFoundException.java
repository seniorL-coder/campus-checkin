package com.wangwei.exception;

/**
 * 活动不存在异常
 */
public class ActivityNotFoundException extends BaseException {

    public ActivityNotFoundException(String msg) {
        super(msg);
    }

}