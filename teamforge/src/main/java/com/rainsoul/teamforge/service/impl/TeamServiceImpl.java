package com.rainsoul.teamforge.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rainsoul.teamforge.common.ErrorCode;
import com.rainsoul.teamforge.exception.BusinessException;
import com.rainsoul.teamforge.model.domain.Team;
import com.rainsoul.teamforge.model.domain.User;
import com.rainsoul.teamforge.model.domain.UserTeam;
import com.rainsoul.teamforge.model.dto.TeamQuery;
import com.rainsoul.teamforge.model.enums.TeamStatusEnum;
import com.rainsoul.teamforge.model.request.TeamJoinRequest;
import com.rainsoul.teamforge.model.request.TeamQuitRequest;
import com.rainsoul.teamforge.model.request.TeamUpdateRequest;
import com.rainsoul.teamforge.model.vo.TeamUserVO;
import com.rainsoul.teamforge.service.TeamService;
import com.rainsoul.teamforge.service.UserService;
import com.rainsoul.teamforge.service.UserTeamService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;

@Service
public class TeamServiceImpl implements TeamService {

    @Resource
    private UserTeamService userTeamService;

    @Resource
    private UserService userService;


    /**
     * 添加队伍
     *
     * @param team 需要添加的队伍对象，包含队伍的各种信息
     * @param loginUser 当前登录的用户，用于标识谁创建了队伍
     * @return 返回创建的队伍的ID
     * @throws BusinessException 如果参数错误、用户未登录、队伍信息不符合要求、用户创建队伍超过限制等情况发生时抛出
     */
    @Override
    public long addTeam(Team team, User loginUser) {
        // 1. 检查队伍对象是否为null
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 2. 检查登录用户对象是否为null
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        long userId = loginUser.getId();
        // 3. 检查队伍最大人数是否符合要求（1 < maxNum <= 20）
        int maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0);
        if (maxNum <= 1 || maxNum > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人数不满足要求");
        }
        // 4. 检查队伍描述是否过长
        String description = team.getDescription();
        if (StringUtils.isNotBlank(description) && description.length() > 512){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍描述过长");
        }
        // 5. 检查队伍状态是否合法
        int status = Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStatusEnum teamStatusEnum = TeamStatusEnum.getEnumByCode(status);
        if (teamStatusEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍状态不满足要求");
        }
        // 6. 如果队伍状态为秘密，则检查密码是否符合要求
        if (teamStatusEnum.equals(TeamStatusEnum.SECRET)) {
            if (StringUtils.isBlank(team.getPassword()) || team.getPassword().length() > 32) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码不能为空或者过长");
            }
        }
        // 7. 检查队伍是否已过期
        Date expireTime = team.getExpireTime();
        if (new Date().after(expireTime)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍已过期");
        }
        // 8. 校验用户创建队伍的数量是否超过限制
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        long hasTeamNum = this.count(queryWrapper);
        if (hasTeamNum >= 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户最多创建 5 个队伍");
        }
        // 9. 插入队伍信息
        team.setId(null);
        team.setUserId(userId);
        boolean result = this.save(team);
        Long teamId = team.getId();
        if (!result || teamId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "创建队伍失败");
        }
        // 10. 插入用户和队伍的关系
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        result = userTeamService.save(userTeam);
        if (!result) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "创建队伍失败");
        }
        return teamId;
    }

    @Override
    public List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin) {
        return List.of();
    }

    @Override
    public boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser) {
        if (teamUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = teamUpdateRequest.getId();
        if (id == null || id < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team oldTeam = getById(id);
        if (oldTeam == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }
        if (oldTeam.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        TeamStatusEnum teamStatusEnum = TeamStatusEnum.getEnumByCode(teamUpdateRequest.getStatus());
        if (teamStatusEnum.equals(TeamStatusEnum.SECRET)){
            if (StringUtils.isBlank(teamUpdateRequest.getPassword()) || teamUpdateRequest.getPassword().length() > 32) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码不能为空或者过长");
            }
        }
        Team updateTeam = new Team();
        BeanUtils.copyProperties(teamUpdateRequest, updateTeam);
        return this.updateById(updateTeam);
    }

    @Override
    public boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser) {
        if (teamJoinRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long teamId = teamJoinRequest.getTeamId();
        //todo 1. 校验请求参数
        return false;
    }

    @Override
    public boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser) {
        return false;
    }

    @Override
    public boolean deleteTeam(long id, User loginUser) {
        return false;
    }

    @Override
    public boolean saveBatch(Collection<Team> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdateBatch(Collection<Team> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean updateBatchById(Collection<Team> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdate(Team entity) {
        return false;
    }

    @Override
    public Team getOne(Wrapper<Team> queryWrapper, boolean throwEx) {
        return null;
    }

    @Override
    public Map<String, Object> getMap(Wrapper<Team> queryWrapper) {
        return Map.of();
    }

    @Override
    public <V> V getObj(Wrapper<Team> queryWrapper, Function<? super Object, V> mapper) {
        return null;
    }

    @Override
    public BaseMapper<Team> getBaseMapper() {
        return null;
    }

    @Override
    public Class<Team> getEntityClass() {
        return null;
    }
}
