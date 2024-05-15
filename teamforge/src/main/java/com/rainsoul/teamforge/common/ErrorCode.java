/**
 * 错误码枚举类，用于定义系统中可能遇到的各种错误码及其相关信息。
 */
package com.rainsoul.teamforge.common;

public enum ErrorCode {
    SUCCESS(0, "ok", ""),
    ERROR_CODE_PASSWORD_NOT_MATCH(40002, "两次输入的密码不一致", ""),
    PARAMS_ERROR(40000, "请求参数错误", ""),
    NULL_ERROR(40001, "请求数据为空", ""),
    NOT_LOGIN(40100, "未登录", ""),
    NO_AUTH(40101, "无权限", ""),
    FORBIDDEN(40301, "禁止操作", ""),
    SYSTEM_ERROR(50000, "系统内部异常", ""),
    STUDENT_ID_FORMAT_ERROR(40003, "学号格式错误", "");

    // 错误码的编号
    private final int code;

    // 错误码对应的信息，用于简短描述错误
    private final String message;

    // 错误码的详细描述
    private final String description;

    /**
     * 构造函数用于初始化错误码及其相关信息。
     *
     * @param code        错误码的编号
     * @param message     错误码对应的信息
     * @param description 错误码的详细描述
     */
    ErrorCode(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    /**
     * 获取错误码的编号。
     *
     * @return 错误码的编号
     */
    public int getCode() {
        return code;
    }

    /**
     * 获取错误码对应的信息。
     *
     * @return 错误码对应的信息
     */
    public String getMessage() {
        return message;
    }

    /**
     * 获取错误码的详细描述。
     *
     * @return 错误码的详细描述
     */
    public String getDescription() {
        return description;
    }
}
