package com.zhao.upms.web.controller;

import com.zhao.upms.common.api.CommonException;
import com.zhao.upms.common.api.CommonResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @Description: 全局异常
 * Author lv bin
 * @date 2017/3/17 9:35
 * version V1.0.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({CommonException.class})
    public CommonResult<String> commonException(CommonException commonException) {
        return CommonResult.failed(commonException.getErrCode(), commonException.getMessage());
    }

}
