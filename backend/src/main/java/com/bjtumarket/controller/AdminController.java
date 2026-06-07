package com.bjtumarket.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bjtumarket.entity.User;
import com.bjtumarket.service.AdminService;
import com.bjtumarket.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "管理员模块")
@RestController
@RequestMapping("/api/admin")
@CrossOrigin
public class AdminController {

    @Autowired
    private AdminService adminService;

    private boolean isTeacher(HttpServletRequest request) {
        Integer userType = (Integer) request.getAttribute("userType");
        return userType != null && userType == 3;
    }

    @ApiOperation("企业入驻列表（分页/状态筛选/搜索）")
    @GetMapping("/enterprise/list")
    public Result<Map<String, Object>> listEnterprises(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {
        if (!isTeacher(request)) {
            return Result.error(403, "无权限访问");
        }
        Page<User> pageResult = adminService.listEnterprises(page, size, status, keyword);
        Map<String, Object> result = new HashMap<>();
        result.put("records", pageResult.getRecords());
        result.put("total", pageResult.getTotal());
        result.put("pages", pageResult.getPages());
        result.put("current", pageResult.getCurrent());
        return Result.success(result);
    }

    @ApiOperation("审核企业（1通过/2拒绝）")
    @PutMapping("/enterprise/audit/{id}")
    public Result<String> auditEnterprise(
            HttpServletRequest request,
            @PathVariable Long id,
            @RequestParam Integer status) {
        if (!isTeacher(request)) {
            return Result.error(403, "无权限访问");
        }
        boolean success = adminService.auditEnterprise(id, status);
        if (!success) {
            return Result.error("审核失败");
        }
        return Result.success("审核成功");
    }

    @ApiOperation("数据概览（学生/企业/岗位/投递/实习率）")
    @GetMapping("/stats/overview")
    public Result<Map<String, Object>> statsOverview(HttpServletRequest request) {
        if (!isTeacher(request)) {
            return Result.error(403, "无权限访问");
        }
        return Result.success(adminService.statsOverview());
    }

    @ApiOperation("各专业实习达成率")
    @GetMapping("/stats/major")
    public Result<Map<String, Object>> statsByMajor(HttpServletRequest request) {
        if (!isTeacher(request)) {
            return Result.error(403, "无权限访问");
        }
        return Result.success(adminService.statsByMajor());
    }

    @ApiOperation("近7天投递趋势")
    @GetMapping("/stats/trend")
    public Result<Map<String, Object>> deliveryTrend(HttpServletRequest request) {
        if (!isTeacher(request)) {
            return Result.error(403, "无权限访问");
        }
        return Result.success(adminService.deliveryTrend());
    }

    @ApiOperation("Top 5 合作企业")
    @GetMapping("/stats/top-enterprises")
    public Result<Map<String, Object>> topEnterprises(HttpServletRequest request) {
        if (!isTeacher(request)) {
            return Result.error(403, "无权限访问");
        }
        return Result.success(adminService.topEnterprises());
    }

    @ApiOperation("Top 5 热门岗位")
    @GetMapping("/stats/hot-jobs")
    public Result<Map<String, Object>> hotJobs(HttpServletRequest request) {
        if (!isTeacher(request)) {
            return Result.error(403, "无权限访问");
        }
        return Result.success(adminService.hotJobs());
    }

    @ApiOperation("实习统计（总人次/进行中/专业分布/Top企业）")
    @GetMapping("/stats/internship")
    public Result<Map<String, Object>> internshipStats(HttpServletRequest request) {
        if (!isTeacher(request)) return Result.error(403, "无权限访问");
        return Result.success(adminService.internshipStats());
    }

    @ApiOperation("企业评价排行（学生对企业的平均评分）")
    @GetMapping("/stats/enterprise-rating")
    public Result<Map<String, Object>> enterpriseRatingStats(HttpServletRequest request) {
        if (!isTeacher(request)) return Result.error(403, "无权限访问");
        return Result.success(adminService.enterpriseRatingStats());
    }

    @ApiOperation("无人投递的过期岗位（超过14天无人投递）")
    @GetMapping("/stats/stale-jobs")
    public Result<Map<String, Object>> staleJobs(HttpServletRequest request) {
        if (!isTeacher(request)) return Result.error(403, "无权限访问");
        return Result.success(adminService.staleJobs());
    }

    @ApiOperation("用户列表（分页/类型筛选/搜索）")
    @GetMapping("/user/list")
    public Result<Map<String, Object>> listUsers(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer userType,
            @RequestParam(required = false) String keyword) {
        if (!isTeacher(request)) return Result.error(403, "无权限访问");
        Page<User> pageResult = adminService.listUsers(page, size, userType, keyword);
        Map<String, Object> result = new HashMap<>();
        result.put("records", pageResult.getRecords());
        result.put("total", pageResult.getTotal());
        result.put("pages", pageResult.getPages());
        result.put("current", pageResult.getCurrent());
        return Result.success(result);
    }

    @ApiOperation("用户统计数据（学生/企业/教师数量）")
    @GetMapping("/user/stats")
    public Result<Map<String, Object>> userStats(HttpServletRequest request) {
        if (!isTeacher(request)) return Result.error(403, "无权限访问");
        return Result.success(adminService.userStats());
    }

    @ApiOperation("切换用户状态（1启用/2禁用）")
    @PutMapping("/user/status/{id}")
    public Result<String> toggleUserStatus(
            HttpServletRequest request,
            @PathVariable Long id,
            @RequestParam Integer status) {
        if (!isTeacher(request)) return Result.error(403, "无权限访问");
        boolean success = adminService.toggleUserStatus(id, status);
        if (!success) return Result.error("操作失败");
        return Result.success("操作成功");
    }
}
