package com.bjtumarket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bjtumarket.entity.CompetitionTeam;
import com.bjtumarket.entity.TeamApplication;
import com.bjtumarket.entity.User;
import com.bjtumarket.mapper.CompetitionTeamMapper;
import com.bjtumarket.mapper.TeamApplicationMapper;
import com.bjtumarket.service.CompetitionService;
import com.bjtumarket.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CompetitionServiceImpl implements CompetitionService {

    @Autowired
    private CompetitionTeamMapper teamMapper;

    @Autowired
    private TeamApplicationMapper applicationMapper;

    @Autowired
    private UserService userService;

    @Override
    public boolean publishTeam(CompetitionTeam team) {
        team.setCreateTime(LocalDateTime.now());
        team.setUpdateTime(LocalDateTime.now());
        team.setStatus(1); // 招募中
        team.setCurrentMembers(1); // 初始只有队长一人
        return teamMapper.insert(team) > 0;
    }

    @Override
    public boolean updateTeam(CompetitionTeam team) {
        team.setUpdateTime(LocalDateTime.now());
        return teamMapper.updateById(team) > 0;
    }

    @Override
    public boolean deleteTeam(Long id) {
        return teamMapper.deleteById(id) > 0;
    }

    @Override
    public Page<CompetitionTeam> listTeams(int page, int size, String keyword, String skillTag) {
        Page<CompetitionTeam> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<CompetitionTeam> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CompetitionTeam::getStatus, 1);
        
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(CompetitionTeam::getTitle, keyword)
                    .or().like(CompetitionTeam::getCompetitionName, keyword)
                    .or().like(CompetitionTeam::getDescription, keyword));
        }
        
        if (StringUtils.hasText(skillTag)) {
            wrapper.like(CompetitionTeam::getSkillTags, skillTag);
        }
        
        wrapper.orderByDesc(CompetitionTeam::getCreateTime);
        return teamMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public CompetitionTeam getTeamById(Long id) {
        return teamMapper.selectById(id);
    }

    @Override
    public List<CompetitionTeam> getMyTeams(Long userId) {
        LambdaQueryWrapper<CompetitionTeam> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CompetitionTeam::getLeaderId, userId)
                .orderByDesc(CompetitionTeam::getCreateTime);
        return teamMapper.selectList(wrapper);
    }

    @Override
    public boolean apply(Long teamId, Long studentId, String note) {
        CompetitionTeam team = teamMapper.selectById(teamId);
        if (team == null || team.getStatus() != 1) {
            return false;
        }

        // 检查是否已申请
        LambdaQueryWrapper<TeamApplication> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeamApplication::getTeamId, teamId)
                .eq(TeamApplication::getStudentId, studentId);
        if (applicationMapper.selectCount(wrapper) > 0) {
            return false;
        }

        TeamApplication application = new TeamApplication();
        application.setTeamId(teamId);
        application.setStudentId(studentId);
        application.setNote(note);
        application.setStatus(0); // 待审核
        application.setCreateTime(LocalDateTime.now());
        application.setUpdateTime(LocalDateTime.now());
        return applicationMapper.insert(application) > 0;
    }

    @Override
    public boolean auditApplication(Long applicationId, Integer status, Long leaderId) {
        TeamApplication application = applicationMapper.selectById(applicationId);
        if (application == null) {
            return false;
        }
        
        CompetitionTeam team = teamMapper.selectById(application.getTeamId());
        if (team == null || !team.getLeaderId().equals(leaderId)) {
            return false;
        }

        application.setStatus(status);
        application.setUpdateTime(LocalDateTime.now());
        
        if (status == 1) { // 如果通过，增加当前成员数
            if (team.getCurrentMembers() >= team.getMaxMembers()) {
                return false; // 已满员
            }
            team.setCurrentMembers(team.getCurrentMembers() + 1);
            if (team.getCurrentMembers().equals(team.getMaxMembers())) {
                team.setStatus(2); // 满员自动关闭招募
            }
            teamMapper.updateById(team);
        }
        
        return applicationMapper.updateById(application) > 0;
    }

    @Override
    public List<Map<String, Object>> getApplicationsByTeam(Long teamId) {
        LambdaQueryWrapper<TeamApplication> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeamApplication::getTeamId, teamId)
                .orderByDesc(TeamApplication::getCreateTime);
        List<TeamApplication> list = applicationMapper.selectList(wrapper);
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (TeamApplication app : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("application", app);
            User student = userService.getById(app.getStudentId());
            if (student != null) {
                map.put("studentName", student.getRealName());
                map.put("studentId", student.getStudentId());
                map.put("phone", student.getPhone());
            }
            result.add(map);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getMyApplications(Long studentId) {
        LambdaQueryWrapper<TeamApplication> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeamApplication::getStudentId, studentId)
                .orderByDesc(TeamApplication::getCreateTime);
        List<TeamApplication> list = applicationMapper.selectList(wrapper);
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (TeamApplication app : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("application", app);
            CompetitionTeam team = teamMapper.selectById(app.getTeamId());
            if (team != null) {
                map.put("teamTitle", team.getTitle());
                map.put("competitionName", team.getCompetitionName());
            }
            result.add(map);
        }
        return result;
    }
}
