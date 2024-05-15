package com.rainsoul.teamforge.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * UserLoginRequest 类用于表示用户登录的请求体。
 * 它包含用户账号和密码信息。
 */
@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L; // 序列化ID，用于版本控制。

    // 用户账号
    private String userAccount;

    // 用户密码
    private String userPassword;
}
