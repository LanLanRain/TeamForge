package com.rainsoul.teamforge.controller;

import com.rainsoul.teamforge.common.BaseResponse;
import com.rainsoul.teamforge.common.ErrorCode;
import com.rainsoul.teamforge.common.ResultUtils;
import com.rainsoul.teamforge.exception.BusinessException;
import com.rainsoul.teamforge.model.request.UserRegisterRequest;
import com.rainsoul.teamforge.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户接口
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String studentId = userRegisterRequest.getStudentId();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, studentId)) {
            return null;
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword, studentId);
        return ResultUtils.success(result);
    }
}
