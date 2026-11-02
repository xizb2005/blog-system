package com.example.demo3.common.enums;

public enum ResultCode {
    // 成功
    SUCCESS(200, "操作成功"),
    // 客户端错误
    FAIL(400, "操作失败"),
    UNAUTHORIZED(401, "未登录，Token无效或过期"),
    FORBIDDEN(403, "权限不足，禁止访问"),
    PARAM_ERROR(405, "参数非法"),
    // 业务自定义异常（给AI调用、业务报错使用）
    BUSINESS_ERROR(50001, "业务执行异常"),
    // 服务端错误
    SERVER_ERROR(500, "服务器异常");

    private Integer code;
    private String msg;

    ResultCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    // getter
    public Integer getCode() {
        return code;
    }
    public String getMsg() {
        return msg;
    }
}