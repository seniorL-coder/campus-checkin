package com.wangwei.result;

import lombok.Data;

import java.io.Serializable;

/**
 * 后端统一返回结果
 * @param <T>
 */
@Data
public class Result<T> implements Serializable {

    private Integer code; //编码：1成功，0和其它数字为失败
    private String msg; //错误信息
    private T data; //数据

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(1);
        result.setData(data);
        result.setMsg("success");
        return result;
    }

    public static Result<Void> success() {
        Result<Void> result = new Result<>();
        result.setCode(1);
        result.setMsg("success");
        return result;
    }

    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<>();
        result.msg = msg;
        result.code = 0;
        return result;
    }

}
