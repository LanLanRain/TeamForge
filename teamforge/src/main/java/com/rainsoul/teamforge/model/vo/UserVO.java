package com.rainsoul.teamforge.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户包装类（脱敏），用于对外提供用户信息，隐藏敏感信息。
 */
@Data
public class UserVO implements Serializable {
    /**
     * 用户的唯一标识符
     */
    private long id;

    /**
     * 用户的昵称或别名
     */
    private String username;

    /**
     * 用户的账号，为了安全起见，不提供真实账号信息
     */
    private String userAccount;

    /**
     * 用户的头像URL
     */
    private String avatarUrl;

    /**
     * 用户的性别，0代表未知，1代表男，2代表女
     */
    private Integer gender;

    /**
     * 用户的电话号码，为了保护隐私，此处不提供真实号码
     */
    private String phone;

    /**
     * 用户的邮箱地址，为了保护隐私，此处不提供真实邮箱地址
     */
    private String email;

    /**
     * 用户的标签列表，以JSON格式存储
     */
    private String tags;

    /**
     * 用户的状态，0代表正常，其他值代表不同状态
     */
    private Integer userStatus;

    /**
     * 用户的创建时间
     */
    private Date createTime;

    /**
     * 用户信息的更新时间
     */
    private Date updateTime;

    /**
     * 用户的角色，0代表普通用户，1代表管理员
     */
    private Integer userRole;

    /**
     * 学生的学号
     */
    private String studentId;

    private static final long serialVersionUID = 1L;
}
