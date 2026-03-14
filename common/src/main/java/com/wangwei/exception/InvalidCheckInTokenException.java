package com.wangwei.exception;

/**
 * 签到凭证无效异常
 */
public class InvalidCheckInTokenException extends BaseException {

    public InvalidCheckInTokenException(String msg) {
        super(msg);
    }

}