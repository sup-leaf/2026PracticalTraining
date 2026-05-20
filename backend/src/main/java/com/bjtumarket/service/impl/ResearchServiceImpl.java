package com.bjtumarket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bjtumarket.entity.ResearchApplication;
import com.bjtumarket.entity.ResearchProject;
import com.bjtumarket.entity.User;
import com.bjtumarket.mapper.ResearchApplicationMapper;
import com.bjtumarket.mapper.ResearchProjectMapper;
import com.bjtumarket.service.ResearchService;
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
public class ResearchServiceImpl implements ResearchService {

    @Autowired
    private ResearchProjectMapper projectMapper;

    @Autowired
    private ResearchApplicationMapper applicationMapper;

    @Autowired
    private UserService userService;

    @Override
    public boolean publishProject(ResearchProject project) {
        project.setCreateTime(LocalDateTime.now());
        project.setUpdateTime(LocalDateTime.now());
        project.setStatus(1); // 招募中
        return projectMapper.insert(project) > 0;
    }

    @Override
    public boolean updateProject(ResearchProject project) {
        project.setUpdateTime(LocalDateTime.now());
        return projectMapper.updateById(project) > 0;
    }

    @Override
    public boolean deleteProject(Long id) {
        return projectMapper.deleteById(id) > 0;
    }

    @Override
    public Page<ResearchProject> listProjects(int page, int size, String keyword) {
        Page<ResearchProject> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<ResearchProject> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ResearchProject::getStatus, 1);
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(ResearchProject::getTitle, keyword)
                    .or().like(ResearchProject::getDescription, keyword));
        }
        wrapper.orderByDesc(ResearchProject::getCreateTime);
        return projectMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public ResearchProject getProjectById(Long id) {
        return projectMapper.selectById(id);
    }

    @Override
    public List<ResearchProject> getMyPublishedProjects(Long teacherId) {
        LambdaQueryWrapper<ResearchProject> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ResearchProject::getPublisherId, teacherId)
                .orderByDesc(ResearchProject::getCreateTime);
        return projectMapper.selectList(wrapper);
    }

    @Override
    public boolean apply(Long projectId, Long studentId, String note) {
        // 检查项目是否存在且处于招募中
        ResearchProject project = projectMapper.selectById(projectId);
        if (project == null || project.getStatus() != 1) {
            return false;
        }

        // 检查是否已申请
        LambdaQueryWrapper<ResearchApplication> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ResearchApplication::getProjectId, projectId)
                .eq(ResearchApplication::getStudentId, studentId);
        if (applicationMapper.selectCount(wrapper) > 0) {
            return false;
        }

        ResearchApplication application = new ResearchApplication();
        application.setProjectId(projectId);
        application.setStudentId(studentId);
        application.setNote(note);
        application.setStatus(0); // 待审核
        application.setCreateTime(LocalDateTime.now());
        application.setUpdateTime(LocalDateTime.now());
        return applicationMapper.insert(application) > 0;
    }

    @Override
    public boolean auditApplication(Long applicationId, Integer status, String note) {
        ResearchApplication application = applicationMapper.selectById(applicationId);
        if (application == null) {
            return false;
        }
        application.setStatus(status);
        application.setNote(note);
        application.setUpdateTime(LocalDateTime.now());
        return applicationMapper.updateById(application) > 0;
    }

    @Override
    public List<Map<String, Object>> getApplicationsByProject(Long projectId) {
        LambdaQueryWrapper<ResearchApplication> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ResearchApplication::getProjectId, projectId)
                .orderByDesc(ResearchApplication::getCreateTime);
        List<ResearchApplication> list = applicationMapper.selectList(wrapper);
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (ResearchApplication app : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("application", app);
            User student = userService.getById(app.getStudentId());
            if (student != null) {
                map.put("studentName", student.getRealName());
                map.put("studentId", student.getStudentId());
            }
            result.add(map);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getMyApplications(Long studentId) {
        LambdaQueryWrapper<ResearchApplication> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ResearchApplication::getStudentId, studentId)
                .orderByDesc(ResearchApplication::getCreateTime);
        List<ResearchApplication> list = applicationMapper.selectList(wrapper);
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (ResearchApplication app : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("application", app);
            ResearchProject project = projectMapper.selectById(app.getProjectId());
            if (project != null) {
                map.put("projectTitle", project.getTitle());
            }
            result.add(map);
        }
        return result;
    }
}
