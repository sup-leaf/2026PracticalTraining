package com.bjtumarket.controller;

import com.bjtumarket.entity.Resume;
import com.bjtumarket.service.ResumeService;
import com.bjtumarket.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/resume")
@CrossOrigin
public class ResumeController {

    @Autowired
    private ResumeService resumeService;

    @GetMapping("/detail")
    public Result<Resume> getResume(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Resume resume = resumeService.getResumeByUserId(userId);
        return Result.success(resume);
    }

    @PostMapping("/save")
    public Result<String> saveResume(@RequestBody Resume resume, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        resume.setUserId(userId);
        boolean success = resumeService.saveOrUpdateResume(resume);
        if (!success) {
            return Result.error("保存失败");
        }
        return Result.success("保存成功");
    }
}
