package com.rainsoul.teamforge.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 队伍和用户信息封装类（脱敏）
 * 提供了关于队伍以及队伍中用户信息的封装，包括基本的队伍信息和用户信息，
 * 以及队伍的状态、创建和更新时间等。
 */
@Data
public class TeamUserVO implements Serializable {

    private static final long serialVersionUID = 1899063007109226944L;

    /**
     * 队伍和用户信息的唯一标识符
     */
    private Long id;

    /**
     * 队伍的名称
     */
    private String name;

    /**
     * 队伍的描述信息
     */
    private String description;

    /**
     * 队伍可容纳的最大成员数量
     */
    private Integer maxNum;

    /**
     * 队伍的有效期，过期时间
     */
    private Date expireTime;

    /**
     * 创建队伍的用户id
     */
    private Long userId;

    /**
     * 队伍的可见性状态：0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;

    /**
     * 队伍的创建时间
     */
    private Date createTime;

    /**
     * 队伍信息的最后更新时间
     */
    private Date updateTime;

    /**
     * 创建该队伍的用户信息
     */
    private UserVO createUser;

    /**
     * 已经加入该队伍的用户数量
     */
    private Integer hasJoinNum;

    /**
     * 标记当前用户是否已经加入了该队伍
     */
    private boolean hasJoin = false;
}
