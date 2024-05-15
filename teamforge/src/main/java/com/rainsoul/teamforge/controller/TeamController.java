package com.rainsoul.teamforge.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rainsoul.teamforge.common.BaseResponse;
import com.rainsoul.teamforge.common.DeleteRequest;
import com.rainsoul.teamforge.common.ErrorCode;
import com.rainsoul.teamforge.common.ResultUtils;
import com.rainsoul.teamforge.exception.BusinessException;
import com.rainsoul.teamforge.model.domain.Team;
import com.rainsoul.teamforge.model.domain.User;
import com.rainsoul.teamforge.model.domain.UserTeam;
import com.rainsoul.teamforge.model.dto.TeamQuery;
import com.rainsoul.teamforge.model.request.TeamAddRequest;
import com.rainsoul.teamforge.model.request.TeamJoinRequest;
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

    /**
     * 查询团队列表，支持分页查询。
     *
     * @param teamQuery 包含查询条件和分页信息的对象。不可为null，否则会抛出参数错误异常。
     * @return 返回一个包含查询结果的分页对象BaseResponse<Page < Team>>，其中Page<Team>是团队的分页结果。
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<Team>> listTeamsByPage(TeamQuery teamQuery) {
        // 校验查询参数是否为null
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 创建一个空的Team对象，并将查询条件复制到该对象中，用于构建查询Wrapper
        Team team = new Team();
        BeanUtils.copyProperties(teamQuery, team);
        // 根据查询参数创建分页对象
        Page<Team> page = new Page<>(teamQuery.getPageNum(), teamQuery.getPageSize());
        // 使用Team对象构建查询条件
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>(team);
        // 执行分页查询
        Page<Team> resultPage = teamService.page(page, queryWrapper);
        // 将查询结果包装成成功响应返回
        return ResultUtils.success(resultPage);
    }

    /**
     * 处理用户加入团队的请求。
     *
     * @param teamJoinRequest 包含用户加入团队的请求信息，如团队ID等。
     * @param request         用户的请求对象，用于获取登录用户信息。
     * @return 返回一个基础响应对象，包含操作结果和团队ID。如果操作成功，团队ID为请求中的团队ID；失败则返回-1。
     */
    @PostMapping("/join")
    public BaseResponse<Long> joinTeam(@RequestBody TeamJoinRequest teamJoinRequest, HttpServletRequest request) {
        if (teamJoinRequest == null) {
            return null;
        }
        // 获取当前登录的用户
        User loginUser = userService.getLoginUser(request);
        // 处理加入团队的请求
        boolean result = teamService.joinTeam(teamJoinRequest, loginUser);
        return ResultUtils.success(result ? teamJoinRequest.getTeamId() : -1);
    }

    /**
     * 删除团队信息
     *
     * @param deleteRequest 包含删除请求信息的对象，其中必须包含一个大于0的id字段
     * @param request       用户的请求对象，用于获取当前登录的用户信息
     * @return 返回一个包含操作结果的BaseResponse对象，成功则result为true，失败则为false
     * @throws BusinessException 如果删除请求对象为null、id不大于0，或删除操作失败时抛出
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        // 执行团队删除操作
        boolean result = teamService.deleteTeam(deleteRequest.getId(), loginUser);
        // 如果删除操作失败，则抛出业务异常
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除失败");
        }
        // 返回删除成功的结果
        return ResultUtils.success(true);
    }

    /**
     * 获取用户创建的团队列表
     *
     * @param request   HttpServletRequest对象，用于获取登录用户信息
     * @param teamQuery 团队查询条件，包含分页、排序等信息，并在方法内设置用户ID
     * @return 返回团队列表的成功响应，包含团队用户详情的列表
     */
    @GetMapping("/list/my/create")
    public BaseResponse<List<TeamUserVO>> listMyCreateTeams(HttpServletRequest request, TeamQuery teamQuery) {
        // 验证查询参数是否为空
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取登录用户信息
        User loginUser = userService.getLoginUser(request);
        // 设置查询条件中的用户ID，用于查询该用户创建的团队
        teamQuery.setUserId(loginUser.getId());
        // 查询团队列表，true表示只查询用户创建的团队
        List<TeamUserVO> teamList = teamService.listTeams(teamQuery, true);
        // 返回查询结果的成功响应
        return ResultUtils.success(teamList);
    }

    /**
     * 获取我加入的团队列表
     *
     * @param request HttpServletRequest对象，用于获取登录用户信息
     * @param teamQuery 团队查询条件，包含分页、排序等信息
     * @return 返回团队列表的响应信息，其中包含我加入的团队详细信息
     */
    @GetMapping("/list/my/join")
    public BaseResponse<List<TeamUserVO>> listMyJoinTeams(HttpServletRequest request, TeamQuery teamQuery) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取登录用户信息
        User loginUser = userService.getLoginUser(request);
        // 查询条件：用户加入的团队
        QueryWrapper<UserTeam> wrapper = new QueryWrapper<>();
        wrapper.eq("userId", loginUser.getId());
        List<UserTeam> userTeamList = userTeamService.list(wrapper);
        // 按团队ID分组，以便后续处理
        Map<Long, List<UserTeam>> listMap = userTeamList.stream().collect(Collectors.groupingBy(UserTeam::getTeamId));
        List<Long> idList = new ArrayList<>(listMap.keySet());
        // 更新查询条件，加入的团队ID列表
        teamQuery.setIdList(idList);
        // 查询并返回团队列表
        List<TeamUserVO> teamList = teamService.listTeams(teamQuery, true);
        return ResultUtils.success(teamList);
    }

}
