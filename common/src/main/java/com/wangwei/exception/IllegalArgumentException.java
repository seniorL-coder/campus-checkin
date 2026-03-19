package com.wangwei.exception;

/**
 * 自定义异常类 - 参数异常
 */
public class IllegalArgumentException extends BaseException{
    public IllegalArgumentException(String message) {
        super(message);
    }
}
