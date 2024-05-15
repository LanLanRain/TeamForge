package com.rainsoul.teamforge.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 * 用于封装用户注册时所需的请求参数
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    /**
     * 用户账号
     * 用户注册时填写的账号信息，用于登录
     */
    private String userAccount;

    /**
     * 用户密码
     * 用户注册时设置的密码，用于登录验证
     */
    private String userPassword;

    /**
     * 确认密码
     * 用户再次输入密码，用于确认密码正确性
     */
    private String checkPassword;

    /**
     * 学生ID
     */
    private String studentId;
}
