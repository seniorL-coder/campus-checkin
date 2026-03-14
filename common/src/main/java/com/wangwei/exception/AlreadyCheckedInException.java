package com.wangwei.exception;

/**
 * 用户已签到异常
 */
public class AlreadyCheckedInException extends BaseException {

    public AlreadyCheckedInException(String msg) {
        super(msg);
    }

}