package com.rainsoul.teamforge.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rainsoul.teamforge.model.domain.User;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * 用户服务接口，提供用户相关的业务操作方法。
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册。
     *
     * @param userAccount   用户账户，唯一标识一个用户。
     * @param userPassword  用户密码，加密存储。
     * @param checkPassword 校验密码，确保输入密码正确。
     * @param studentId     学生ID，用于学生用户的身份验证（可选）。
     * @return 新用户id。
     */
    long userRegister(String userAccount, String userPassword, String checkPassword, String studentId);

    /**
     * 用户登录。
     *
     * @param userAccount  用户账户。
     * @param userPassword 用户密码。
     * @param request      HTTP请求对象，用于获取客户端信息。
     * @return 脱敏后的用户信息。
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 对用户信息进行脱敏处理。
     *
     * @param originUser 原始用户信息。
     * @return 脱敏后的用户信息。
     */
    User getSafetyUser(User originUser);

    /**
     * 用户注销。
     *
     * @param request HTTP请求对象，用于操作与请求相关的用户会话。
     * @return 操作结果，1表示成功。
     */
    int userLogout(HttpServletRequest request);

    /**
     * 根据标签搜索用户。
     *
     * @param tagNameList 标签名称列表，用于筛选用户。
     * @return 匹配标签的用户列表。
     */
    List<User> searchUsersByTags(List<String> tagNameList);

    /**
     * 更新用户信息。
     *
     * @param user         需要更新的用户信息。
     * @param loginUser    当前登录用户信息。
     * @return 更新操作的结果，1表示成功。
     */
    int updateUser(User user, User loginUser);

    /**
     * 获取当前登录用户信息。
     *
     * @param request HTTP请求对象，用于获取当前请求的登录用户信息。
     * @return 当前登录用户的信息。
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 判断当前请求用户是否为管理员。
     *
     * @param request HTTP请求对象，用于获取请求的用户信息。
     * @return 如果是管理员返回true，否则返回false。
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 判断用户是否为管理员。
     *
     * @param loginUser 登录用户信息。
     * @return 如果是管理员返回true，否则返回false。
     */
    boolean isAdmin(User loginUser);

    /**
     * 匹配用户。
     *
     * @param num 匹配的用户数量。
     * @param loginUser 当前登录用户。
     * @return 匹配到的用户列表。
     */
    List<User> matchUsers(long num, User loginUser);
}

