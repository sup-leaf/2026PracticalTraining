package com.bjtumarket.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bjtumarket.entity.Job;
import com.bjtumarket.service.JobService;
import com.bjtumarket.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/job")
@CrossOrigin
public class JobController {

    @Autowired
    private JobService jobService;

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

    @GetMapping("/detail/{id}")
    public Result<Job> getJobDetail(@PathVariable Long id) {
        Job job = jobService.getJobDetail(id);
        if (job == null) {
            return Result.error("岗位不存在");
        }
        return Result.success(job);
    }

    @PostMapping("/publish")
    public Result<String> publishJob(@RequestBody Job job, HttpServletRequest request) {
        Long publisherId = (Long) request.getAttribute("userId");
        Integer userType = (Integer) request.getAttribute("userType");
        job.setPublisherId(publisherId);
        job.setPublisherType(userType == 2 ? 1 : 2);
        boolean success = jobService.publishJob(job);
        if (!success) {
            return Result.error("发布失败");
        }
        return Result.success("发布成功");
    }

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

    @DeleteMapping("/delete/{id}")
    public Result<String> deleteJob(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        boolean success = jobService.deleteJob(id, userId);
        if (!success) {
            return Result.error("删除失败");
        }
        return Result.success("删除成功");
    }

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
}
