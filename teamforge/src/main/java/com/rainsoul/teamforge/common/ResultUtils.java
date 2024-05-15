package com.rainsoul.teamforge.common;

/**
 * 提供用于生成操作结果的工具方法。
 */
public class ResultUtils {

    /**
     * 生成表示操作成功的响应对象。
     *
     * @param data 操作成功时返回的数据。
     * @param <T>  数据类型。
     * @return 带有成功标识、数据和成功消息的响应对象。
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0, data, "ok");
    }

    /**
     * 生成表示操作失败的响应对象，使用预定义的错误代码。
     *
     * @param errorCode 错误代码枚举，包含操作失败的错误码和消息。
     * @return 带有失败标识、错误码和消息的响应对象。
     */
    public static BaseResponse error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode);
    }

    /**
     * 生成表示操作失败的响应对象，使用自定义的错误码、错误消息和描述。
     *
     * @param code      操作失败的错误码。
     * @param message   错误消息。
     * @param description 错误的详细描述。
     * @return 带有失败标识、错误码、错误消息和描述的响应对象。
     */
    public static BaseResponse error(int code, String message, String description) {
        return new BaseResponse(code, null, message, description);
    }

    /**
     * 生成表示操作失败的响应对象，使用预定义的错误代码和自定义的错误描述。
     *
     * @param errorCode 错误代码枚举，包含操作失败的错误码和消息。
     * @param message   错误消息。
     * @param description 错误的详细描述。
     * @return 带有失败标识、错误码、错误消息和描述的响应对象。
     */
    public static BaseResponse error(ErrorCode errorCode, String message, String description) {
        return new BaseResponse(errorCode.getCode(), null, message, description);
    }

    /**
     * 生成表示操作失败的响应对象，使用预定义的错误代码和自定义的错误描述。
     *
     * @param errorCode 错误代码枚举，包含操作失败的错误码和消息。
     * @param description 错误的详细描述。
     * @return 带有失败标识、错误码、错误消息和描述的响应对象。
     */
    public static BaseResponse error(ErrorCode errorCode, String description) {
        return new BaseResponse(errorCode.getCode(), errorCode.getMessage(), description);
    }
}
