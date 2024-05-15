package com.rainsoul.teamforge.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rainsoul.teamforge.common.ErrorCode;
import com.rainsoul.teamforge.exception.BusinessException;
import com.rainsoul.teamforge.mapper.UserMapper;
import com.rainsoul.teamforge.model.domain.User;
import com.rainsoul.teamforge.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "CQUPT";

    /**
     * 用户注册接口
     *
     * @param userAccount 用户账号，不能包含特殊字符
     * @param userPassword 用户密码，长度必须大于等于8位
     * @param checkPassword 确认密码，必须和userPassword一致
     * @param studentId 学生ID，长度必须为10位
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

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        return null;
    }

    @Override
    public User getSafetyUser(User originUser) {
        return null;
    }

    @Override
    public int userLogout(HttpServletRequest request) {
        return 0;
    }

    @Override
    public List<User> searchUsersByTags(List<String> tagNameList) {
        return List.of();
    }

    @Override
    public int updateUser(User user, User loginUser) {
        return 0;
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        return null;
    }

    @Override
    public boolean isAdmin(HttpServletRequest request) {
        return false;
    }

    @Override
    public boolean isAdmin(User loginUser) {
        return false;
    }

    @Override
    public List<User> matchUsers(long num, User loginUser) {
        return List.of();
    }

    @Override
    public boolean saveBatch(Collection<User> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdateBatch(Collection<User> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean updateBatchById(Collection<User> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdate(User entity) {
        return false;
    }

    @Override
    public User getOne(Wrapper<User> queryWrapper, boolean throwEx) {
        return null;
    }

    @Override
    public Map<String, Object> getMap(Wrapper<User> queryWrapper) {
        return Map.of();
    }

    @Override
    public <V> V getObj(Wrapper<User> queryWrapper, Function<? super Object, V> mapper) {
        return null;
    }

    @Override
    public BaseMapper<User> getBaseMapper() {
        return null;
    }

    @Override
    public Class<User> getEntityClass() {
        return null;
    }
}
