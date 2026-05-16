package com.bjtumarket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bjtumarket.entity.User;
import com.bjtumarket.mapper.DeliveryMapper;
import com.bjtumarket.mapper.JobMapper;
import com.bjtumarket.mapper.ResumeMapper;
import com.bjtumarket.mapper.UserMapper;
import com.bjtumarket.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JobMapper jobMapper;

    @Autowired
    private DeliveryMapper deliveryMapper;

    @Autowired
    private ResumeMapper resumeMapper;

    @Override
    public Page<User> listEnterprises(Integer page, Integer size, Integer status, String keyword) {
        Page<User> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUserType, 2);
        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.like(User::getCompanyName, keyword);
        }
        wrapper.orderByDesc(User::getCreateTime);
        return userMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public boolean auditEnterprise(Long enterpriseId, Integer status) {
        User user = userMapper.selectById(enterpriseId);
        if (user == null || !user.getUserType().equals(2)) {
            return false;
        }
        user.setStatus(status);
        return userMapper.updateById(user) > 0;
    }

    @Override
    public Map<String, Object> statsOverview() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("studentCount", userMapper.countStudents());
        result.put("enterpriseCount", userMapper.countApprovedEnterprises());
        result.put("pendingEnterpriseCount", userMapper.countPendingEnterprises());
        result.put("jobCount", jobMapper.countActiveJobs());
        result.put("deliveryCountThisMonth", deliveryMapper.countThisMonth());

        long totalStudents = userMapper.countStudents();
        List<Map<String, Object>> majorStats = resumeMapper.countByMajor();
        long acceptedTotal = 0;
        long resumeTotal = 0;
        for (Map<String, Object> row : majorStats) {
            acceptedTotal += ((Number) row.getOrDefault("acceptedCount", 0)).longValue();
            resumeTotal += ((Number) row.getOrDefault("studentCount", 0)).longValue();
        }
        result.put("internshipRate", resumeTotal > 0
                ? Math.round(acceptedTotal * 10000.0 / resumeTotal) / 100.0
                : 0);
        return result;
    }

    @Override
    public Map<String, Object> statsByMajor() {
        List<Map<String, Object>> list = resumeMapper.countByMajor();
        for (Map<String, Object> row : list) {
            long studentCount = ((Number) row.getOrDefault("studentCount", 0)).longValue();
            long acceptedCount = ((Number) row.getOrDefault("acceptedCount", 0)).longValue();
            double rate = studentCount > 0 ? Math.round(acceptedCount * 10000.0 / studentCount) / 100.0 : 0;
            row.put("rate", rate);
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("majors", list);
        return result;
    }

    @Override
    public Map<String, Object> deliveryTrend() {
        List<Map<String, Object>> list = deliveryMapper.deliveryTrend();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("trend", list);
        return result;
    }

    @Override
    public Map<String, Object> topEnterprises() {
        List<Map<String, Object>> list = jobMapper.topEnterprises();
        int rank = 1;
        for (Map<String, Object> row : list) {
            row.put("rank", rank++);
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("enterprises", list);
        return result;
    }

    @Override
    public Map<String, Object> hotJobs() {
        List<Map<String, Object>> list = jobMapper.hotJobs();
        int rank = 1;
        for (Map<String, Object> row : list) {
            row.put("rank", rank++);
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("jobs", list);
        return result;
    }
}
