package com.example.demo3.common;

import com.example.demo3.common.enums.ResultCode;
import lombok.Data;

@Data
public class Result<T> {
    private Integer code;
    private String msg;
    private T data;

    public static <T> Result<T> success(){
        return success(null);
    }

    public static <T> Result<T> success(T data){
        Result<T> r = new Result<>();
        r.setCode(ResultCode.SUCCESS.getCode());
        r.setMsg(ResultCode.SUCCESS.getMsg());
        r.setData(data);
        return r;
    }

    public static <T> Result<T> fail(ResultCode resultCode){
        return fail(resultCode.getCode(), resultCode.getMsg());
    }

    public static <T> Result<T> fail(Integer code, String msg){
        Result<T> r = new Result<>();
        r.setCode(code);
        r.setMsg(msg);
        r.setData(null);
        return r;
    }

    public static <T> Result<T> error(String msg){
        return fail(ResultCode.SERVER_ERROR.getCode(), msg);
    }
}