package com.rainsoul.teamforge.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rainsoul.teamforge.common.ErrorCode;
import com.rainsoul.teamforge.exception.BusinessException;
import com.rainsoul.teamforge.mapper.UserMapper;
import com.rainsoul.teamforge.model.domain.User;
import com.rainsoul.teamforge.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.rainsoul.teamforge.constant.UserConstant.ADMIN_ROLE;
import static com.rainsoul.teamforge.constant.UserConstant.USER_LOGIN_STATE;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "CQUPT";

    /**
     * 用户注册接口
     *
     * @param userAccount   用户账号，不能包含特殊字符
     * @param userPassword  用户密码，长度必须大于等于8位
     * @param checkPassword 确认密码，必须和userPassword一致
     * @param studentId     学生ID，长度必须为10位
     * @return 注册成功返回用户ID，失败返回-1
     * @throws BusinessException 当参数错误、密码不匹配、账号或学号重复时抛出
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword, String studentId) {
        // 参数校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, studentId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码过短");
        }
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.ERROR_CODE_PASSWORD_NOT_MATCH, "两次输入的密码不一致");
        }
        if (studentId.length() != 10) {
            throw new BusinessException(ErrorCode.STUDENT_ID_FORMAT_ERROR, "学号格式错误");
        }

        // 校验账户名称是否包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;,\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户不能包含特殊字符");
        }

        // 检查用户账号是否已存在
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("userAccount", userAccount);
        long count = userMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
        }

        // 检查学生ID是否已存在
        wrapper = new QueryWrapper<>();
        wrapper.eq("studentId", studentId);
        count = userMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "学号重复");
        }

        // 密码加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        // 用户信息插入数据库
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setStudentId(studentId);

        // 用户信息保存
        boolean saveResult = this.save(user);
        if (!saveResult) {
            return -1;
        }
        return user.getId();
    }

    /**
     * 用户登录逻辑处理。
     *
     * @param userAccount  用户账号，不能为空或全为空格。
     * @param userPassword 用户密码，长度必须大于等于8个字符，不能包含特殊字符。
     * @param request      用户登录请求，用于记录用户登录态。
     * @return 登录成功返回脱敏后的用户对象，否则返回null。
     */
    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 基本信息校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null;
        }
        if (userPassword.length() < 8) {
            return null;
        }
        // 账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;,\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            return null;
        }
        // 2. 密码加密处理
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            return null;
        }
        // 3. 用户信息脱敏处理
        User safetyUser = getSafetyUser(user);
        // 4. 设置用户登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);
        return safetyUser;
    }

    /**
     * 创建一个安全的用户对象副本。
     * 该方法通过复制原始用户对象的所有属性到一个新的用户对象中，来创建一个安全的用户对象副本。
     * 注意：如果原始用户对象为null，该方法将返回null。
     *
     * @param originUser 原始用户对象，不可为null。
     * @return 安全的用户对象副本，如果输入为null，则返回null。
     */
    @Override
    public User getSafetyUser(User originUser) {
        if (originUser == null) {
            return null;
        }
        // 创建一个新的用户对象，用于存放安全的用户信息
        User safetyUser = new User();

        // 复制原始用户的所有信息到安全用户对象中
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setStudentId(originUser.getStudentId());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setCreateTime(originUser.getCreateTime());
        safetyUser.setTags(originUser.getTags());

        return safetyUser;
    }


    @Override
    public int userLogout(HttpServletRequest request) {
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

    /**
     * 根据标签名列表搜索用户。
     *
     * @param tagNameList 标签名列表，不能为空。
     * @return 匹配指定标签名的用户列表。
     * @throws BusinessException 如果输入的标签名列表为空，抛出此异常。
     */
    @Override
    public List<User> searchUsersByTags(List<String> tagNameList) {
        // 检查输入的标签名列表是否为空，为空则抛出业务异常
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 创建查询包装器
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        // 从数据库获取所有用户列表
        List<User> userList = userMapper.selectList(wrapper);

        // 使用Gson工具将用户标签从字符串转换为Set<String>
        Gson gson = new Gson();
        // 过滤用户列表，保留所有包含所有指定标签的用户
        return userList.stream().filter(user -> {
                    String tags = user.getTags();
                    /**
                     * 将JSON字符串转换为字符串集。
                     * 该方法使用Gson库将提供的JSON字符串解析为Set<String>类型的集合。
                     *
                     * @param tags JSON字符串，表示一组标签。
                     * @return 解析后的字符串集，每个字符串代表一个标签。
                     */
                    // Todo 学习Gson
                    Set<String> tempTagNameSet = gson.fromJson(tags, new TypeToken<Set<String>>() {
                    }.getType());
                    // 检查用户是否包含所有指定的标签
                    for (String tagName : tagNameList) {
                        if (!tempTagNameSet.contains(tagName)) {
                            return false; // 如果用户缺少某个标签，则过滤掉该用户
                        }
                    }
                    return true;
                }).map(this::getSafetyUser) // 对过滤后的用户进行安全处理
                .collect(Collectors.toList()); // 收集结果并返回
    }

    /**
     * 更新用户信息。
     *
     * @param user      需要更新的用户对象。
     * @param loginUser 当前登录的用户对象。
     * @return 更新成功返回1，否则返回0。
     * @throws BusinessException 如果用户ID不合法、没有权限操作或用户不存在，则抛出业务异常。
     */
    @Override
    public int updateUser(User user, User loginUser) {
        long userId = user.getId();
        // 检查用户ID是否合法
        if (userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 检查是否有权限更新用户信息
        if (userId != loginUser.getId() || !isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        // 根据ID查询原用户信息，确保用户存在
        User originalUser = userMapper.selectById(userId);
        if (originalUser == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        // 更新用户信息并返回更新结果
        return userMapper.updateById(user);
    }


    /**
     * 从请求中获取登录的用户。
     *
     * @param request HttpServletRequest对象，用于获取会话信息。
     * @return 返回经过安全处理的User对象，如果用户未登录或参数为空则返回null。
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 检查请求对象是否为空
        if (request == null) {
            return null;
        }
        // 从会话中获取当前登录的用户对象
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        // 如果用户对象不存在，则返回null
        if (user == null) {
            return null;
        }
        // 对用户对象进行安全处理后返回
        return this.getSafetyUser(user);
    }

    /**
     * 检查当前用户是否为管理员。
     *
     * @param request HttpServletRequest对象，用于获取会话中的用户信息。
     * @return boolean 返回用户是否为管理员。如果是管理员，返回true；否则返回false。
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        // 从会话中获取登录的用户对象
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        // 判断用户对象是否存在且角色为管理员
        return user != null && user.getUserRole() == ADMIN_ROLE;
    }


    /**
     * 检查登录的用户是否为管理员。
     *
     * @param loginUser 登录的用户对象。
     * @return 如果用户是管理员，返回true；否则返回false。
     */
    @Override
    public boolean isAdmin(User loginUser) {
        // 检查登录用户是否不为空，并且其角色为管理员
        return loginUser != null && loginUser.getUserRole() == ADMIN_ROLE;
    }


    @Override
    public List<User> matchUsers(long num, User loginUser) {
        return List.of();
    }


    /**
     * 根据标签搜索用户（SQL 查询版）
     * <p>
     * 该方法通过给定的标签名列表查询用户。查询条件是用户拥有的标签中包含列表中的所有标签。
     * 注意：该方法使用了SQL的like操作进行标签匹配，可能效率较低，且存在SQL注入的风险。
     * </p>
     *
     * @param tagNameList 用户要拥有的标签列表。不能为空。
     * @return 返回经过处理的安全用户列表。
     * @throws BusinessException 如果标签名列表为空，抛出此异常。
     */
    @Deprecated
    private List<User> searchUsersByTagsBySQL(List<String> tagNameList) {
        // 检查标签名列表是否为空，为空则抛出业务异常
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        // 拼接多个标签的like查询条件
        for (String tagName : tagNameList) {
            queryWrapper = queryWrapper.like("tags", tagName);
        }
        // 执行查询，并对每个用户结果进行安全处理
        List<User> userList = userMapper.selectList(queryWrapper);
        return userList.stream().map(this::getSafetyUser).collect(Collectors.toList());
    }

}
