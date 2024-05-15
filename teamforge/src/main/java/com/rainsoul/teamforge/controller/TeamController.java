package com.rainsoul.teamforge.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rainsoul.teamforge.common.BaseResponse;
import com.rainsoul.teamforge.common.ErrorCode;
import com.rainsoul.teamforge.common.ResultUtils;
import com.rainsoul.teamforge.exception.BusinessException;
import com.rainsoul.teamforge.model.domain.Team;
import com.rainsoul.teamforge.model.domain.User;
import com.rainsoul.teamforge.model.domain.UserTeam;
import com.rainsoul.teamforge.model.dto.TeamQuery;
import com.rainsoul.teamforge.model.request.TeamAddRequest;
import com.rainsoul.teamforge.model.request.TeamUpdateRequest;
import com.rainsoul.teamforge.model.vo.TeamUserVO;
import com.rainsoul.teamforge.service.TeamService;
import com.rainsoul.teamforge.service.UserService;
import com.rainsoul.teamforge.service.UserTeamService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 队伍接口
 */
@RestController
@RequestMapping("/team")
@Slf4j
public class TeamController {

    @Resource
    private UserService userService;

    @Resource
    private TeamService teamService;

    @Resource
    private UserTeamService userTeamService;

    /**
     * 添加团队信息
     *
     * @param teamAddRequest 包含团队添加信息的请求体
     * @param request        用户的请求信息，用于获取登录用户信息
     * @return 添加团队成功的响应，包含新团队的ID
     * @throws BusinessException 如果传入的团队添加请求体为null，则抛出业务异常
     */
    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request) {
        if (teamAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        // 创建新的团队对象，并复制请求体中的属性到团队对象
        Team team = new Team();
        BeanUtils.copyProperties(teamAddRequest, team);
        long teamId = teamService.addTeam(team, loginUser);
        return ResultUtils.success(teamId);
    }

    /**
     * 更新团队信息
     *
     * @param teamUpdateRequest 包含更新团队所需信息的请求体
     * @param request           HttpServletRequest对象，用于获取当前登录用户
     * @return 返回一个基础响应对象，包含更新是否成功的标志
     * @throws BusinessException 如果输入参数错误或更新失败，抛出业务异常
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest, HttpServletRequest request) {
        if (teamUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.updateTeam(teamUpdateRequest, loginUser);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新失败");
        }
        return ResultUtils.success(true);
    }

    /**
     * 根据团队ID获取团队信息。
     *
     * @param id 团队的唯一标识符，必须为正数。
     * @return 返回一个包含团队信息的BaseResponse对象，如果团队不存在，则返回错误信息。
     */
    @GetMapping("/get")
    public BaseResponse<Team> getTeamById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = teamService.getById(id);
        // 团队信息不存在时，抛出空错误异常
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return ResultUtils.success(team);
    }

    /**
     * 获取队伍列表 todo 待完善
     *
     * @param teamQuery 查询条件，包含分页、排序等信息
     * @param request   用户的请求，用于获取登录用户信息和判断是否为管理员
     * @return 返回队伍列表的响应信息，包含队伍列表和是否已加入队伍的标志
     */
    @GetMapping("/list")
    public BaseResponse<List<TeamUserVO>> listTeams(TeamQuery teamQuery, HttpServletRequest request) {
        // 判断查询条件是否为空，为空则抛出参数错误异常
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 判断当前用户是否为管理员
        boolean isAdmin = userService.isAdmin(request);
        // 根据查询条件和是否为管理员，获取队伍列表
        List<TeamUserVO> teamList = teamService.listTeams(teamQuery, isAdmin);
        // 提取队伍ID列表，用于后续查询用户加入的队伍
        List<Long> teamIdList = teamList.stream().map(TeamUserVO::getId).collect(Collectors.toList());
        // 查询登录用户已加入的队伍
        QueryWrapper<UserTeam> wrapper = new QueryWrapper<>();
        try {
            User loginUser = userService.getLoginUser(request);
            wrapper.eq("userId", loginUser.getId());
            wrapper.in("teamId", teamIdList);
            List<UserTeam> userTeamList = userTeamService.list(wrapper);
            // 提取已加入的队伍ID集合
            Set<Long> hasJoinTeamIdSet = userTeamList.stream().map(UserTeam::getTeamId)
                    .collect(Collectors.toSet());
            // 标记每支队伍是否已加入
            teamList.forEach(team -> {
                boolean hasJoin = hasJoinTeamIdSet.contains(team.getId());
                team.setHasJoin(hasJoin);
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // 查询已加入每个队伍的用户数量
        QueryWrapper<UserTeam> userTeamJoinQueryWrapper = new QueryWrapper<>();
        userTeamJoinQueryWrapper.in("teamId", teamIdList);
        List<UserTeam> userTeamList = userTeamService.list(userTeamJoinQueryWrapper);
        // 组合队伍ID和加入该队伍的用户列表
        Map<Long, List<UserTeam>> teamIdUserTeamList = userTeamList.stream().collect(Collectors.groupingBy(UserTeam::getTeamId));
        // 设置每支队伍已加入人数
        teamList.forEach(team -> team.setHasJoinNum(teamIdUserTeamList.getOrDefault(team.getId(), new ArrayList<>()).size()));
        // 返回成功响应，包含标记了是否已加入以及已加入人数的队伍列表
        return ResultUtils.success(teamList);
    }


}
