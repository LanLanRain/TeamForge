package com.rainsoul.teamforge.exception;

import com.rainsoul.teamforge.common.ErrorCode;

/**
 * 自定义业务异常类，用于在发生业务错误时抛出。
 * 包含错误代码（code）、错误描述（description）以及Java运行时异常的基本功能。
 */
public class BusinessException extends RuntimeException {

    private final int code; // 错误代码
    private final String description; // 错误描述

    /**
     * 构造一个带有自定义错误消息、错误代码和错误描述的BusinessException。
     *
     * @param message 错误消息，描述发生了什么错误。
     * @param code 错误代码，用于标识错误的唯一整数。
     * @param description 错误描述，详细说明错误的含义。
     */
    public BusinessException(String message, int code, String description) {
        super(message);
        this.code = code;
        this.description = description;
    }

    /**
     * 构造一个基于ErrorCode枚举的BusinessException实例。
     *
     * @param errorCode 一个包含错误代码、消息和描述的ErrorCode枚举实例。
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = errorCode.getDescription();
    }

    /**
     * 构造一个基于ErrorCode枚举和自定义错误描述的BusinessException实例。
     *
     * @param errorCode 一个包含错误代码和消息的ErrorCode枚举实例。
     * @param description 自定义的错误描述。
     */
    public BusinessException(ErrorCode errorCode, String description) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = description;
    }

    /**
     * 获取错误代码。
     *
     * @return 错误代码的整数。
     */
    public int getCode() {
        return code;
    }

    /**
     * 获取错误描述。
     *
     * @return 错误描述的字符串。
     */
    public String getDescription() {
        return description;
    }
}
