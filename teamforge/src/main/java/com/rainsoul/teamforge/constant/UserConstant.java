package com.rainsoul.teamforge.constant;

/**
 * 用户常量接口，定义了与用户相关的常量。
 */
public interface UserConstant {

    /**
     * 获取用户登录态键的字符串值。
     * 该常量用于标识用户登录状态的键，在存储或检索用户登录状态时使用。
     */
    String USER_LOGIN_STATE = "userLoginState";

    //  ------- 权限 --------

    /**
     * 获取默认权限的整数值。
     * 该常量定义了系统中默认用户的权限等级。
     */
    int DEFAULT_ROLE = 0;

    /**
     * 获取管理员权限的整数值。
     * 该常量定义了系统中管理员用户的权限等级。
     */
    int ADMIN_ROLE = 1;

}
