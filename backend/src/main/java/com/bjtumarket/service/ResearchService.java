package com.bjtumarket.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bjtumarket.entity.ResearchApplication;
import com.bjtumarket.entity.ResearchProject;

import java.util.List;
import java.util.Map;

public interface ResearchService {
    // 项目管理
    boolean publishProject(ResearchProject project);
    boolean updateProject(ResearchProject project);
    boolean deleteProject(Long id);
    Page<ResearchProject> listProjects(int page, int size, String keyword);
    ResearchProject getProjectById(Long id);
    List<ResearchProject> getMyPublishedProjects(Long teacherId);

    // 申请管理
    boolean apply(Long projectId, Long studentId, String note);
    boolean auditApplication(Long applicationId, Integer status, String note);
    List<Map<String, Object>> getApplicationsByProject(Long projectId);
    List<Map<String, Object>> getMyApplications(Long studentId);
}
