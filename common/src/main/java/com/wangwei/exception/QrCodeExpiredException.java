package com.wangwei.exception;

/**
 * 二维码已失效异常
 */
public class QrCodeExpiredException extends BaseException {

    public QrCodeExpiredException(String msg) {
        super(msg);
    }

}