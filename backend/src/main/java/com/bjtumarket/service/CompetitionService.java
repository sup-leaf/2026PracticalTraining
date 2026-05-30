package com.bjtumarket.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bjtumarket.entity.CompetitionTeam;
import com.bjtumarket.entity.TeamApplication;

import java.util.List;
import java.util.Map;

public interface CompetitionService {
    // 队伍管理
    boolean publishTeam(CompetitionTeam team);
    boolean updateTeam(CompetitionTeam team);
    boolean deleteTeam(Long id);
    Page<CompetitionTeam> listTeams(int page, int size, String keyword, String skillTag);
    CompetitionTeam getTeamById(Long id);
    List<CompetitionTeam> getMyTeams(Long userId);

    // 申请管理
    boolean apply(Long teamId, Long studentId, String note);
    boolean auditApplication(Long applicationId, Integer status, Long leaderId);
    List<Map<String, Object>> getApplicationsByTeam(Long teamId);
    List<Map<String, Object>> getMyApplications(Long studentId);
}
