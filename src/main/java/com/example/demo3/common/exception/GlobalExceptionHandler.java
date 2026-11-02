package com.example.demo3.common.exception;

import com.example.demo3.common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e){
        return Result.fail(e.getCode(), e.getMsg());
    }

    @ExceptionHandler(org.springframework.validation.BindException.class)
    public Result<?> handleValidException(org.springframework.validation.BindException e){
        String message = e.getFieldError().getDefaultMessage();
        return Result.fail(400, message);
    }

    @ExceptionHandler(ThirdPartyApiException.class)
    public Result<?> thirdPartyApiHandler(ThirdPartyApiException e){
        log.error("第三方API异常", e);
        return Result.fail(5001, "AI服务暂时繁忙，请稍后重试");
    }

    @ExceptionHandler(ResourceAccessException.class)
    public Result<?> timeOutHandler(ResourceAccessException e){
        log.error("请求超时异常", e);
        return Result.fail(5002, "AI请求超时，请稍后重试");
    }

    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e){
        // Deleted:e.printStackTrace();
        log.error("系统未知异常", e);
        return Result.fail(500, "系统内部错误");
    }
}