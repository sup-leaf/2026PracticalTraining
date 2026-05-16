package com.bjtumarket.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bjtumarket.entity.User;
import com.bjtumarket.service.AdminService;
import com.bjtumarket.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @GetMapping("/stats/overview")
    public Result<Map<String, Object>> statsOverview(HttpServletRequest request) {
        if (!isTeacher(request)) {
            return Result.error(403, "无权限访问");
        }
        return Result.success(adminService.statsOverview());
    }

    @GetMapping("/stats/major")
    public Result<Map<String, Object>> statsByMajor(HttpServletRequest request) {
        if (!isTeacher(request)) {
            return Result.error(403, "无权限访问");
        }
        return Result.success(adminService.statsByMajor());
    }

    @GetMapping("/stats/trend")
    public Result<Map<String, Object>> deliveryTrend(HttpServletRequest request) {
        if (!isTeacher(request)) {
            return Result.error(403, "无权限访问");
        }
        return Result.success(adminService.deliveryTrend());
    }

    @GetMapping("/stats/top-enterprises")
    public Result<Map<String, Object>> topEnterprises(HttpServletRequest request) {
        if (!isTeacher(request)) {
            return Result.error(403, "无权限访问");
        }
        return Result.success(adminService.topEnterprises());
    }

    @GetMapping("/stats/hot-jobs")
    public Result<Map<String, Object>> hotJobs(HttpServletRequest request) {
        if (!isTeacher(request)) {
            return Result.error(403, "无权限访问");
        }
        return Result.success(adminService.hotJobs());
    }
}
