package com.bjtumarket.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "岗位模块")
@RestController
@RequestMapping("/api/job")
@CrossOrigin
public class JobController {

    @Autowired
    private JobService jobService;

    @Autowired
    private UserService userService;

    @ApiOperation("岗位列表（分页/类型/关键词筛选）")
    @GetMapping("/list")
    public Result<Map<String, Object>> listJobs(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer jobType,
            @RequestParam(required = false) String keyword) {
        Page<Job> pageResult = jobService.listJobs(page, size, jobType, keyword);
        Map<String, Object> result = new HashMap<>();
        result.put("records", pageResult.getRecords());
        result.put("total", pageResult.getTotal());
        result.put("pages", pageResult.getPages());
        result.put("current", pageResult.getCurrent());
        return Result.success(result);
    }

    @ApiOperation("岗位详情")
    @GetMapping("/detail/{id}")
    public Result<Job> getJobDetail(@PathVariable Long id) {
        Job job = jobService.getJobDetail(id);
        if (job == null) {
            return Result.error("岗位不存在");
        }
        return Result.success(job);
    }

    @ApiOperation("发布岗位")
    @PostMapping("/publish")
    public Result<String> publishJob(@RequestBody Job job, HttpServletRequest request) {
        Long publisherId = (Long) request.getAttribute("userId");
        Integer userType = (Integer) request.getAttribute("userType");
        // S3: 校外企业检查发布数量限制（当月最多5条）
        if (userType == 2) {
            User publisher = userService.getById(publisherId);
            if (publisher != null && (publisher.getCooperationType() == null || publisher.getCooperationType() == 2)) {
                List<Job> existing = jobService.lambdaQuery()
                        .eq(Job::getPublisherId, publisherId)
                        .eq(Job::getStatus, 1)
                        .ge(Job::getCreateTime, LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0))
                        .list();
                if (existing.size() >= 5) return Result.error("校外企业当月发布已达上限（5条）");
            }
        }
        job.setPublisherId(publisherId);
        job.setPublisherType(userType == 2 ? 1 : 2);
        boolean success = jobService.publishJob(job);
        return success ? Result.success("发布成功") : Result.error("发布失败");
    }

    @ApiOperation("编辑岗位")
    @PutMapping("/update")
    public Result<String> updateJob(@RequestBody Job job, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Job existing = jobService.getById(job.getId());
        if (existing == null || !existing.getPublisherId().equals(userId)) {
            return Result.error("无权操作");
        }
        boolean success = jobService.updateJob(job);
        if (!success) {
            return Result.error("更新失败");
        }
        return Result.success("更新成功");
    }

    @ApiOperation("删除岗位")
    @DeleteMapping("/delete/{id}")
    public Result<String> deleteJob(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        boolean success = jobService.deleteJob(id, userId);
        if (!success) {
            return Result.error("删除失败");
        }
        return Result.success("删除成功");
    }

    @ApiOperation("我发布的岗位")
    @GetMapping("/my")
    public Result<Map<String, Object>> myJobs(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = (Long) request.getAttribute("userId");
        Page<Job> pageResult = jobService.myJobs(userId, page, size);
        Map<String, Object> result = new HashMap<>();
        result.put("records", pageResult.getRecords());
        result.put("total", pageResult.getTotal());
        return Result.success(result);
    }

    @ApiOperation("岗位地图数据（按城市聚合统计）")
    @GetMapping("/map")
    public Result<List<Map<String, Object>>> jobMap() {
        return Result.success(jobService.getJobMapData());
    }
}
