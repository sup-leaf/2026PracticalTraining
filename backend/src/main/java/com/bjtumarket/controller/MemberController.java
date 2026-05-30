package com.bjtumarket.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bjtumarket.entity.Job;
import com.bjtumarket.entity.User;
import com.bjtumarket.service.JobService;
import com.bjtumarket.service.UserService;
import com.bjtumarket.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "会员模块")
@RestController
@RequestMapping("/api/member")
@CrossOrigin
public class MemberController {

    @Autowired
    private JobService jobService;

    @Autowired
    private UserService userService;

    @ApiOperation("VIP专属提醒：最新匹配岗位/科研/竞赛")
    @GetMapping("/alerts")
    public Result<Map<String, Object>> vipAlerts(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Integer userType = (Integer) request.getAttribute("userType");
        if (userType == null || userType != 1) return Result.error(403, "仅学生可用");
        Map<String, Object> result = new HashMap<>();
        LambdaQueryWrapper<Job> w = new LambdaQueryWrapper<>();
        w.eq(Job::getStatus, 1).orderByDesc(Job::getCreateTime).last("LIMIT 3");
        List<Job> jobs = jobService.list(w);
        result.put("latestJobs", jobs);
        return Result.success(result);
    }

    @ApiOperation("查询当前用户会员等级")
    @GetMapping("/level")
    public Result<Map<String, Object>> myLevel(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        User user = userService.getById(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("memberLevel", user != null ? user.getMemberLevel() : 0);
        return Result.success(result);
    }

    @ApiOperation("切换会员等级（0免费↔1VIP，测试用）")
    @PutMapping("/toggle")
    public Result<Map<String, Object>> toggleVip(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Integer userType = (Integer) request.getAttribute("userType");
        if (userType == null || userType != 1) return Result.error(403, "仅学生可用");
        User user = userService.getById(userId);
        if (user == null) return Result.error("用户不存在");
        int newLevel = (user.getMemberLevel() == null || user.getMemberLevel() == 0) ? 1 : 0;
        user.setMemberLevel(newLevel);
        userService.updateById(user);
        Map<String, Object> result = new HashMap<>();
        result.put("memberLevel", newLevel);
        result.put("message", newLevel == 1 ? "已升级为VIP" : "已降为普通用户");
        return Result.success(result);
    }
}
