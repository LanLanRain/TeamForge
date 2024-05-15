package com.rainsoul.teamforge.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rainsoul.teamforge.common.BaseResponse;
import com.rainsoul.teamforge.common.ErrorCode;
import com.rainsoul.teamforge.common.ResultUtils;
import com.rainsoul.teamforge.exception.BusinessException;
import com.rainsoul.teamforge.model.domain.User;
import com.rainsoul.teamforge.model.request.UserRegisterRequest;
import com.rainsoul.teamforge.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.rainsoul.teamforge.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户接口
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户注册接口
     *
     * @param userRegisterRequest 包含用户注册所需信息的请求体
     *                            - userAccount 用户账号
     *                            - userPassword 用户密码
     *                            - checkPassword 确认密码
     *                            - studentId 学生ID
     * @return 返回注册结果的BaseResponse对象，其中包含注册成功后的用户ID
     * @throws BusinessException 如果注册请求参数有误或为空，则抛出业务异常
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        // 校验注册请求对象是否为空
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 提取注册请求中的各项参数
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String studentId = userRegisterRequest.getStudentId();
        // 校验参数是否为空
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, studentId)) {
            return null;
        }
        // 调用服务层执行用户注册，并返回注册结果
        long result = userService.userRegister(userAccount, userPassword, checkPassword, studentId);
        return ResultUtils.success(result);
    }

    /**
     * 用户登录接口
     *
     * @param userRegisterRequest 包含用户登录所需信息的请求体，如用户名和密码
     * @param request             用户的HTTP请求，可用于获取额外的请求信息
     * @return BaseResponse<User> 登录结果的响应体，成功时包含用户信息，失败时包含错误信息
     */
    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserRegisterRequest userRegisterRequest, HttpServletRequest request) {
        // 检查传入的登录请求体是否为null
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        // 检查用户名和密码是否为空
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "参数错误");
        }
        // 执行用户登录逻辑
        User user = userService.userLogin(userAccount, userPassword, request);
        // 返回登录成功的结果
        return ResultUtils.success(user);
    }

    /**
     * 处理用户登出请求的函数。
     *
     * @param request HttpServletRequest对象，用于获取用户登出请求的详细信息。
     * @return 返回一个包含登出操作结果的BaseResponse对象，其中结果代码为操作影响的行数。
     * 如果请求对象为null，会抛出一个业务异常。
     */
    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        // 检查请求对象是否为null，为null则抛出业务异常
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 调用userService的userLogout方法处理用户登出，返回处理结果
        int result = userService.userLogout(request);
        // 构造并返回一个包含登出操作结果的success响应
        return ResultUtils.success(result);
    }

    /**
     * 获取当前登录用户的信息。
     *
     * @param request HttpServletRequest对象，用于获取会话信息。
     * @return BaseResponse<User> 包含用户信息的响应对象，如果用户未登录，则返回错误信息。
     */
    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        // 从会话中获取登录用户对象
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        // 如果当前用户对象为空，表示用户未登录，抛出业务异常
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        // 获取当前用户的ID
        long userId = currentUser.getId();
        // 通过用户ID从数据库获取用户信息
        User user = userService.getById(userId);
        // 对用户信息进行安全处理后返回
        User safetyUser = userService.getSafetyUser(user);
        return ResultUtils.success(safetyUser);
    }

    /**
     * 搜索用户信息。
     *
     * @param username 搜索关键字，用于查询用户名中包含该关键字的用户。
     * @param request  HttpServletRequest对象，用于判断当前用户是否为管理员。
     * @return 返回一个包含搜索结果的BaseResponse对象，其中搜索结果为User类型的列表。
     * 如果不是管理员，抛出BusinessException异常。
     */
    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username, HttpServletRequest request) {
        // 判断请求用户是否为管理员，如果不是则抛出异常
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 创建查询条件，如果username不为空，则添加like条件
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }
        // 根据查询条件查询用户列表
        List<User> userList = userService.list(queryWrapper);
        // 对查询结果进行处理，获取脱密用户信息
        List<User> list = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        // 返回处理后的结果
        return ResultUtils.success(list);
    }

    /**
     * 根据标签名列表搜索用户。
     *
     * @param tagNameList 标签名列表，可选参数。如果为空，则抛出参数错误异常。
     * @return 返回一个包含搜索到的用户列表的BaseResponse对象。
     */
    @GetMapping("/search/tags")
    public BaseResponse<List<User>> searchUsersByTags(@RequestParam(required = false) List<String> tagNameList) {
        // 检查传入的标签名列表是否为空，为空则抛出业务异常
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<User> userList = userService.searchUsersByTags(tagNameList);
        // 构造并返回成功响应，包含搜索到的用户列表
        return ResultUtils.success(userList);
    }

    /**
     * 更新用户信息。
     *
     * @param user    包含更新后的用户信息的对象，通过RequestBody接收前端传来的JSON数据。
     * @param request HttpServletRequest对象，用于获取当前登录用户信息。
     * @return 返回操作结果的BaseResponse对象，其中包含更新成功的用户数量。
     * @throws BusinessException 如果传入的用户对象为null，抛出参数错误的业务异常。
     */
    @PostMapping("/update")
    public BaseResponse<Integer> updateUser(@RequestBody User user, HttpServletRequest request) {
        // 校验传入的用户信息对象是否为空
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        // 如果是管理员，允许更新任意用户
        // 如果不是管理员，只允许更新当前（自己的）信息
        // 执行用户信息更新操作
        int result = userService.updateUser(user, loginUser);
        // 返回更新操作的成功数量
        return ResultUtils.success(result);
    }

    /**
     * 删除用户接口
     *
     * @param id 用户ID，通过RequestBody接收
     * @param request HttpServletRequest对象，用于判断请求用户是否为管理员
     * @return 返回操作结果，操作成功返回true，失败返回false
     * @throws BusinessException 如果用户没有权限或用户ID参数错误，则抛出业务异常
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody Long id, HttpServletRequest request) {
        // 判断请求用户是否为管理员
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        // 校验用户ID是否合法
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 删除用户
        boolean result = userService.removeById(id);
        return ResultUtils.success(result);
    }


}
