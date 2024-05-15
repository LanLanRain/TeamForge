package com.rainsoul.teamforge.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rainsoul.teamforge.model.domain.Team;
import com.rainsoul.teamforge.model.domain.User;
import com.rainsoul.teamforge.model.dto.TeamQuery;
import com.rainsoul.teamforge.model.request.TeamJoinRequest;
import com.rainsoul.teamforge.model.request.TeamQuitRequest;
import com.rainsoul.teamforge.model.request.TeamUpdateRequest;
import com.rainsoul.teamforge.model.vo.TeamUserVO;

import java.util.List;

/**
 * 队伍服务接口，定义了与队伍相关的业务操作。
 */
public interface TeamService extends IService<Team> {

    /**
     * 创建队伍。
     *
     * @param team     队伍信息
     * @param loginUser 登录用户信息
     * @return 新队伍的ID
     */
    long addTeam(Team team, User loginUser);

    /**
     * 搜索队伍。
     *
     * @param teamQuery     搜索条件
     * @param isAdmin        是否是管理员搜索
     * @return 符合条件的队伍列表
     */
    List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin);

    /**
     * 更新队伍信息。
     *
     * @param teamUpdateRequest 更新请求信息
     * @param loginUser         登录用户信息
     * @return 更新成功与否
     */
    boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser);

    /**
     * 用户加入队伍。
     *
     * @param teamJoinRequest 加入请求信息
     * @param loginUser       登录用户信息
     * @return 加入成功与否
     */
    boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser);

    /**
     * 用户退出队伍。
     *
     * @param teamQuitRequest 退出请求信息
     * @param loginUser       登录用户信息
     * @return 退出成功与否
     */
    boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser);

    /**
     * 删除（解散）队伍。
     *
     * @param id      队伍ID
     * @param loginUser 登录用户信息
     * @return 删除成功与否
     */
    boolean deleteTeam(long id, User loginUser);
}
