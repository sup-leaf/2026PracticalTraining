package com.bjtumarket.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bjtumarket.entity.User;

import java.util.Map;

public interface AdminService {
    Page<User> listEnterprises(Integer page, Integer size, Integer status, String keyword);

    boolean auditEnterprise(Long enterpriseId, Integer status);

    Page<User> listUsers(Integer page, Integer size, Integer userType, String keyword);

    Map<String, Object> userStats();

    boolean toggleUserStatus(Long userId, Integer status);

    Map<String, Object> statsOverview();

    Map<String, Object> statsByMajor();

    Map<String, Object> deliveryTrend();

    Map<String, Object> topEnterprises();

    Map<String, Object> hotJobs();

    Map<String, Object> internshipStats();

    Map<String, Object> enterpriseRatingStats();

    Map<String, Object> staleJobs();
}
