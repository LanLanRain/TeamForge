package com.rainsoul.teamforge.model.enums;

/**
 * 团队状态枚举
 * 提供了团队状态的三种选项：公开、私有和加密
 */
public enum TeamStatusEnum {
    PUBLIC(0, "公开"), // 公开的团队状态
    PRIVATE(1, "私有"), // 私有的团队状态
    SECRET(2, "加密"); // 加密的团队状态

    private int code; // 状态码
    private String message; // 状态对应的消息

    /**
     * 通过状态码获取对应的枚举实例
     *
     * @param code 状态码
     * @return 对应的枚举实例，如果找不到则返回null
     */
    public static TeamStatusEnum getEnumByCode(Integer code) {
        if (code == null) {
            return null;
        }
        TeamStatusEnum[] values = TeamStatusEnum.values();
        for (TeamStatusEnum teamStatusEnum : values) {
            if (teamStatusEnum.getCode() == code) {
                return teamStatusEnum;
            }
        }
        return null;
    }

    /**
     * 构造函数，初始化团队状态枚举实例
     *
     * @param code 状态码
     * @param message 状态对应的消息
     */
    TeamStatusEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 获取状态码
     *
     * @return 状态码
     */
    public int getCode() {
        return code;
    }

    /**
     * 设置状态码
     *
     * @param code 新的状态码
     */
    public void setCode(int code) {
        this.code = code;
    }

    /**
     * 获取状态对应的消息
     *
     * @return 状态对应的消息
     */
    public String getMessage() {
        return message;
    }

    /**
     * 设置状态对应的消息
     *
     * @param message 新的状态消息
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
