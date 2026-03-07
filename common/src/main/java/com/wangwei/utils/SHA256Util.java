package com.wangwei.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256Util {

    private SHA256Util() {
    } // 私有化构造器


    public static String encrypt(String str) {
        String encryptStr;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(str.getBytes());
            encryptStr = String.format("%064x", new BigInteger(1, hash));
        } catch (NoSuchAlgorithmException e) {
            // 这里捕获异常并处理，可以记录日志或选择默认行为
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }

        return encryptStr;
    }

}
