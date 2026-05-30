package com.bjtumarket.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bjtumarket.entity.CompetitionTeam;
import com.bjtumarket.service.CompetitionService;
import com.bjtumarket.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "竞赛组队模块")
@RestController
@RequestMapping("/api/competition")
@CrossOrigin
public class CompetitionController {

    @Autowired
    private CompetitionService competitionService;

    private Long getUserId(HttpServletRequest request) {
        return (Long) request.getAttribute("userId");
    }

    private Integer getUserType(HttpServletRequest request) {
        return (Integer) request.getAttribute("userType");
    }

    @ApiOperation("发布组队招募")
    @PostMapping("/publish")
    public Result<String> publishTeam(@RequestBody CompetitionTeam team, HttpServletRequest request) {
        if (getUserType(request) != 1) {
            return Result.error(403, "只有学生可以发布组队招募");
        }
        team.setLeaderId(getUserId(request));
        boolean success = competitionService.publishTeam(team);
        return success ? Result.success("发布成功") : Result.error("发布失败");
    }

    @ApiOperation("组队招募列表")
    @GetMapping("/list")
    public Result<Map<String, Object>> listTeams(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String skillTag) {
        Page<CompetitionTeam> pageResult = competitionService.listTeams(page, size, keyword, skillTag);
        Map<String, Object> result = new HashMap<>();
        result.put("records", pageResult.getRecords());
        result.put("total", pageResult.getTotal());
        result.put("pages", pageResult.getPages());
        result.put("current", pageResult.getCurrent());
        return Result.success(result);
    }

    @ApiOperation("获取组队详情")
    @GetMapping("/{id}")
    public Result<CompetitionTeam> getTeam(@PathVariable Long id) {
        CompetitionTeam team = competitionService.getTeamById(id);
        return team != null ? Result.success(team) : Result.error("队伍不存在");
    }

    @ApiOperation("申请入队")
    @PostMapping("/apply")
    public Result<String> apply(@RequestParam Long teamId, @RequestParam String note, HttpServletRequest request) {
        if (getUserType(request) != 1) {
            return Result.error(403, "只有学生可以申请入队");
        }
        boolean success = competitionService.apply(teamId, getUserId(request), note);
        return success ? Result.success("申请成功") : Result.error("申请失败，可能已申请或队伍已停止招募");
    }

    @ApiOperation("队长审核申请")
    @PostMapping("/audit")
    public Result<String> auditApplication(
            @RequestParam Long applicationId,
            @RequestParam Integer status,
            HttpServletRequest request) {
        boolean success = competitionService.auditApplication(applicationId, status, getUserId(request));
        return success ? Result.success("操作成功") : Result.error("操作失败，权限不足或队伍已满员");
    }

    @ApiOperation("队长查看本队申请列表")
    @GetMapping("/team/{teamId}/applications")
    public Result<List<Map<String, Object>>> getApplications(@PathVariable Long teamId, HttpServletRequest request) {
        CompetitionTeam team = competitionService.getTeamById(teamId);
        if (team == null || !team.getLeaderId().equals(getUserId(request))) {
            return Result.error(403, "无权限查看");
        }
        return Result.success(competitionService.getApplicationsByTeam(teamId));
    }

    @ApiOperation("学生查看我的申请记录")
    @GetMapping("/my/applications")
    public Result<List<Map<String, Object>>> getMyApplications(HttpServletRequest request) {
        return Result.success(competitionService.getMyApplications(getUserId(request)));
    }

    @ApiOperation("我发布的组队")
    @GetMapping("/my/teams")
    public Result<List<CompetitionTeam>> getMyTeams(HttpServletRequest request) {
        return Result.success(competitionService.getMyTeams(getUserId(request)));
    }
}
