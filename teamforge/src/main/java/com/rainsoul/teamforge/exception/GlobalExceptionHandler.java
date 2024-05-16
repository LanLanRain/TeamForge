package com.rainsoul.teamforge.exception;

import com.rainsoul.teamforge.common.BaseResponse;
import com.rainsoul.teamforge.common.ErrorCode;
import com.rainsoul.teamforge.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理类，用于捕获并处理应用中抛出的各种异常。
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理业务异常。
     * @param e 抛出的业务异常对象，包含错误代码、消息和描述。
     * @return 返回一个包含错误信息的BaseResponse对象。
     */
    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException e) {
        log.error("businessException: " + e.getMessage(), e); // 记录业务异常日志
        return ResultUtils.error(e.getCode(), e.getMessage(), e.getDescription()); // 返回业务异常响应
    }

    /**
     * 处理运行时异常。
     * @param e 抛出的运行时异常对象，包含错误消息。
     * @return 返回一个包含系统错误信息的BaseResponse对象。
     */
    @ExceptionHandler(RuntimeException.class)
    public BaseResponse runtimeExceptionHandler(RuntimeException e) {
        log.error("runtimeException", e); // 记录运行时异常日志
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, e.getMessage(), ""); // 返回运行时异常响应
    }
}

