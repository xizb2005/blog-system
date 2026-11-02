package com.example.demo3.common.exception;


import com.example.demo3.common.enums.ResultCode;

public class BusinessException extends RuntimeException{
    private Integer code;
    private String msg;

    public BusinessException(Integer code, String msg){
        super(msg);
        this.code = code;
        this.msg = msg;
    }
    public BusinessException(String message){
        this(ResultCode.BUSINESS_ERROR.getCode(),message);
    }
    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}