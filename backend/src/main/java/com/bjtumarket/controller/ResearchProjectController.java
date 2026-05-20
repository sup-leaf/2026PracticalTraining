package com.bjtumarket.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bjtumarket.entity.ResearchProject;
import com.bjtumarket.service.ResearchService;
import com.bjtumarket.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "科研项目选人模块")
@RestController
@RequestMapping("/api/research")
@CrossOrigin
public class ResearchProjectController {

    @Autowired
    private ResearchService researchService;

    private Long getUserId(HttpServletRequest request) {
        return (Long) request.getAttribute("userId");
    }

    private Integer getUserType(HttpServletRequest request) {
        return (Integer) request.getAttribute("userType");
    }

    @ApiOperation("教师发布科研项目招募")
    @PostMapping("/project/publish")
    public Result<String> publishProject(@RequestBody ResearchProject project, HttpServletRequest request) {
        if (getUserType(request) != 3) {
            return Result.error(403, "只有教师可以发布科研项目");
        }
        project.setPublisherId(getUserId(request));
        boolean success = researchService.publishProject(project);
        return success ? Result.success("发布成功") : Result.error("发布失败");
    }

    @ApiOperation("科研项目招募列表")
    @GetMapping("/project/list")
    public Result<Map<String, Object>> listProjects(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        Page<ResearchProject> pageResult = researchService.listProjects(page, size, keyword);
        Map<String, Object> result = new HashMap<>();
        result.put("records", pageResult.getRecords());
        result.put("total", pageResult.getTotal());
        return Result.success(result);
    }

    @ApiOperation("获取项目详情")
    @GetMapping("/project/{id}")
    public Result<ResearchProject> getProject(@PathVariable Long id) {
        ResearchProject project = researchService.getProjectById(id);
        return project != null ? Result.success(project) : Result.error("项目不存在");
    }

    @ApiOperation("学生申请加入科研项目")
    @PostMapping("/apply")
    public Result<String> apply(@RequestParam Long projectId, @RequestParam String note, HttpServletRequest request) {
        if (getUserType(request) != 1) {
            return Result.error(403, "只有学生可以申请科研项目");
        }
        boolean success = researchService.apply(projectId, getUserId(request), note);
        return success ? Result.success("申请成功") : Result.error("申请失败，可能已申请或项目已关闭");
    }

    @ApiOperation("教师审核申请")
    @PostMapping("/application/audit")
    public Result<String> auditApplication(
            @RequestParam Long applicationId,
            @RequestParam Integer status,
            @RequestParam(required = false) String note,
            HttpServletRequest request) {
        if (getUserType(request) != 3) {
            return Result.error(403, "只有教师可以审核申请");
        }
        boolean success = researchService.auditApplication(applicationId, status, note);
        return success ? Result.success("操作成功") : Result.error("操作失败");
    }

    @ApiOperation("教师获取项目的申请列表")
    @GetMapping("/project/{projectId}/applications")
    public Result<List<Map<String, Object>>> getApplications(@PathVariable Long projectId, HttpServletRequest request) {
        if (getUserType(request) != 3) {
            return Result.error(403, "无权限查看");
        }
        return Result.success(researchService.getApplicationsByProject(projectId));
    }

    @ApiOperation("学生查看我的申请记录")
    @GetMapping("/my/applications")
    public Result<List<Map<String, Object>>> getMyApplications(HttpServletRequest request) {
        if (getUserType(request) != 1) {
            return Result.error(403, "无权限查看");
        }
        return Result.success(researchService.getMyApplications(getUserId(request)));
    }

    @ApiOperation("教师查看我发布的项目")
    @GetMapping("/my/projects")
    public Result<List<ResearchProject>> getMyProjects(HttpServletRequest request) {
        if (getUserType(request) != 3) {
            return Result.error(403, "无权限查看");
        }
        return Result.success(researchService.getMyPublishedProjects(getUserId(request)));
    }
}
