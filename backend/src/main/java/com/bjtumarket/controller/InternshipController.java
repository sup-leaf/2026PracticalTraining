package com.bjtumarket.controller;

import com.bjtumarket.entity.Internship;
import com.bjtumarket.entity.InternshipLog;
import com.bjtumarket.service.InternshipService;
import com.bjtumarket.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 实习全过程管理控制器
 * 学生端：发起实习、提交日志、查看证明
 * 企业端：查看实习生、评分
 * 教师端：统计（见 AdminController）
 */
@Api(tags = "实习模块")
@RestController
@RequestMapping("/api/internship")
@CrossOrigin
public class InternshipController {

    @Autowired
    private InternshipService internshipService;

    /**
     * 学生发起实习
     * 参数 deliveryId —— 必须是被录用(status=3)的投递
     */
    @ApiOperation("学生发起实习（仅已录用投递，同时限一个进行中）")
    @PostMapping("/start")
    public Result<Internship> start(HttpServletRequest request, @RequestParam Long deliveryId) {
        Long userId = (Long) request.getAttribute("userId");
        Internship internship = internshipService.startInternship(userId, deliveryId);
        if (internship == null) {
            return Result.error("实习开始失败（投递未被录用或已有进行中的实习）");
        }
        return Result.success(internship);
    }

    /**
     * 学生查看自己全部实习记录
     */
    @ApiOperation("查看全部实习记录（按时间倒序）")
    @GetMapping("/my")
    public Result<List<Internship>> myInternship(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(internshipService.getMyInternships(userId));
    }

    /**
     * 学生提交每周实习日志
     */
    @ApiOperation("提交实习周日志")
    @PostMapping("/log")
    public Result<String> submitLog(HttpServletRequest request,
                                     @RequestParam Long internshipId,
                                     @RequestParam Integer weekNum,
                                     @RequestParam String content) {
        Long userId = (Long) request.getAttribute("userId");
        boolean ok = internshipService.submitLog(internshipId, userId, weekNum, content);
        return ok ? Result.success("提交成功") : Result.error("提交失败");
    }

    /**
     * 查看某实习的所有日志
     */
    @ApiOperation("查看实习日志列表")
    @GetMapping("/logs")
    public Result<List<InternshipLog>> getLogs(@RequestParam Long internshipId) {
        return Result.success(internshipService.getLogs(internshipId));
    }

    /**
     * 学生主动结束实习
     */
    @ApiOperation("结束实习")
    @PutMapping("/end")
    public Result<String> end(HttpServletRequest request, @RequestParam Long internshipId) {
        Long userId = (Long) request.getAttribute("userId");
        boolean ok = internshipService.endInternship(internshipId, userId);
        return ok ? Result.success("已结束") : Result.error("操作失败");
    }

    /**
     * 企业/教师对实习评分（学生不可评分）
     */
    @ApiOperation("企业/教师评分（1-5分+评语）")
    @PutMapping("/rate")
    public Result<String> rate(HttpServletRequest request,
                                @RequestParam Long internshipId,
                                @RequestParam Integer rating,
                                @RequestParam(required = false) String review) {
        Long userId = (Long) request.getAttribute("userId");
        Integer userType = (Integer) request.getAttribute("userType");
        boolean ok = internshipService.rateInternship(internshipId, userId, userType, rating, review);
        return ok ? Result.success("评分成功") : Result.error("评分失败，请确认你不是学生身份");
    }

    /**
     * 查看实习证明数据
     */
    @ApiOperation("生成实习证明数据")
    @GetMapping("/certificate/{id}")
    public Result<Map<String, Object>> certificate(@PathVariable Long id) {
        Map<String, Object> data = internshipService.getCertificateData(id);
        return data.isEmpty() ? Result.error("实习不存在") : Result.success(data);
    }

    /**
     * 企业查看自己发布岗位下的实习生列表
     */
    @ApiOperation("企业查看实习生列表")
    @GetMapping("/publisher")
    public Result<List<Map<String, Object>>> publisherInternships(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(internshipService.getPublisherInternships(userId));
    }
}
