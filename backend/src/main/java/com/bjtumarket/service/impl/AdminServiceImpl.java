package com.bjtumarket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bjtumarket.entity.User;
import com.bjtumarket.entity.Job;
import com.bjtumarket.entity.InternshipStudentReview;
import com.bjtumarket.mapper.DeliveryMapper;
import com.bjtumarket.mapper.InternshipMapper;
import com.bjtumarket.mapper.InternshipStudentReviewMapper;
import com.bjtumarket.mapper.JobMapper;
import com.bjtumarket.mapper.ResumeMapper;
import com.bjtumarket.mapper.UserMapper;
import com.bjtumarket.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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

    @Autowired
    private InternshipMapper internshipMapper;

    @Autowired
    private InternshipStudentReviewMapper studentReviewMapper;

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
    @CacheEvict(value = "adminStats", allEntries = true)
    public boolean auditEnterprise(Long enterpriseId, Integer status) {
        User user = userMapper.selectById(enterpriseId);
        if (user == null || !user.getUserType().equals(2)) {
            return false;
        }
        user.setStatus(status);
        return userMapper.updateById(user) > 0;
    }

    @Override
    @Cacheable(value = "adminStats", key = "'overview'")
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
        result.put("internshipTotal", internshipMapper.countAll());
        result.put("internshipActive", internshipMapper.countActive());

        // S6.1: 预警——低于75%的专业
        List<Map<String, Object>> warnings = new ArrayList<>();
        for (Map<String, Object> row : majorStats) {
            long studentCount = ((Number) row.getOrDefault("studentCount", 0)).longValue();
            long acceptedCount = ((Number) row.getOrDefault("acceptedCount", 0)).longValue();
            double rate = studentCount > 0 ? Math.round(acceptedCount * 10000.0 / studentCount) / 100.0 : 0;
            if (rate < 75 && studentCount > 0) {
                Map<String, Object> w = new HashMap<>();
                w.put("major", row.get("major"));
                w.put("rate", rate);
                w.put("threshold", 75);
                warnings.add(w);
            }
        }
        result.put("warnings", warnings);
        return result;
    }

    @Override
    @Cacheable(value = "adminStats", key = "'byMajor'")
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
    @Cacheable(value = "adminStats", key = "'trend'")
    public Map<String, Object> deliveryTrend() {
        List<Map<String, Object>> list = deliveryMapper.deliveryTrend();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("trend", list);
        return result;
    }

    @Override
    @Cacheable(value = "adminStats", key = "'topEnterprises'")
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
    @Cacheable(value = "adminStats", key = "'hotJobs'")
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

    @Override
    @Cacheable(value = "adminStats", key = "'internship'")
    public Map<String, Object> internshipStats() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalInternships", internshipMapper.countAll());
        result.put("activeInternships", internshipMapper.countActive());

        List<Map<String, Object>> majorList = internshipMapper.countByMajor();
        for (Map<String, Object> row : majorList) {
            if (row.get("avgRating") != null) {
                double avg = ((Number) row.get("avgRating")).doubleValue();
                row.put("avgRating", Math.round(avg * 10.0) / 10.0);
            }
        }
        result.put("majorDistribution", majorList);
        result.put("topCompanies", internshipMapper.topCompanies());
        return result;
    }

    @Override
    @Cacheable(value = "adminStats", key = "'enterpriseRating'")
    public Map<String, Object> enterpriseRatingStats() {
        Map<String, Object> result = new LinkedHashMap<>();
        List<InternshipStudentReview> reviews = studentReviewMapper.selectList(null);
        long total = reviews.size();
        double avg = reviews.stream().mapToInt(InternshipStudentReview::getRating).average().orElse(0);
        result.put("totalReviews", total);
        result.put("averageRating", Math.round(avg * 10.0) / 10.0);
        return result;
    }

    @Override
    @Cacheable(value = "adminStats", key = "'staleJobs'")
    public Map<String, Object> staleJobs() {
        List<Job> jobs = jobMapper.selectList(
            new LambdaQueryWrapper<Job>()
                .eq(Job::getStatus, 1)
                .eq(Job::getDeliveryCount, 0)
                .lt(Job::getCreateTime, java.time.LocalDateTime.now().minusDays(14))
        );
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("staleJobs", jobs);
        result.put("count", jobs.size());
        return result;
    }
}
