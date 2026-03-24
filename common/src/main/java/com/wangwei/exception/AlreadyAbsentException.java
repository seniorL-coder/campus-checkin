package com.wangwei.exception;

/**
 *  已经缺勤异常类
 */
public class AlreadyAbsentException extends BaseException {
    public AlreadyAbsentException() {
        super();
    }

    public AlreadyAbsentException(String message) {
        super(message);
    }
}
