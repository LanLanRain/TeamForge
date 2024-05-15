package com.rainsoul.teamforge.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用返回类，用于封装后端方法的返回结果。
 */
@Data
public class BaseResponse<T> implements Serializable {

    private int code; // 返回状态码
    private T data; // 返回的数据
    private String message; // 返回的消息，用于描述操作结果
    private String description; // 返回的详细描述，用于进一步解释操作结果

    /**
     * 完整构造函数，初始化所有字段。
     *
     * @param code    状态码
     * @param data    返回的数据
     * @param message 消息
     * @param description 描述
     */
    public BaseResponse(int code, T data, String message, String description) {
        this.code = code;
        this.data = data;
        this.message = message;
        this.description = description;
    }

    /**
     * 简化构造函数，不包含描述信息。
     *
     * @param code    状态码
     * @param data    返回的数据
     * @param message 消息
     */
    public BaseResponse(int code, T data, String message) {
        this(code, data, message, "");
    }

    /**
     * 最简构造函数，仅包含状态码和数据。
     *
     * @param code    状态码
     * @param data    返回的数据
     */
    public BaseResponse(int code, T data) {
        this(code, data, "", "");
    }

    /**
     * 通过ErrorCode对象构造BaseResponse对象。
     *
     * @param errorCode 错误码对象，包含状态码、消息和描述
     */
    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage(), errorCode.getDescription());
    }
}
