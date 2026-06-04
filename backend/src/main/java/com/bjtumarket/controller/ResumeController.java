package com.bjtumarket.controller;

import com.bjtumarket.entity.Resume;
import com.bjtumarket.service.AIService;
import com.bjtumarket.service.ResumeService;
import com.bjtumarket.util.CosUtil;
import com.bjtumarket.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Api(tags = "简历模块")
@RestController
@RequestMapping("/api/resume")
@CrossOrigin
public class ResumeController {

    @Autowired
    private ResumeService resumeService;

    @Autowired
    private AIService aiService;

    @Autowired
    private CosUtil cosUtil;

    @Value("${cos.base-url:}")
    private String cosBaseUrl;

    @ApiOperation("查看我的简历")
    @GetMapping("/detail")
    public Result<Resume> getResume(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Resume resume = resumeService.getResumeByUserId(userId);
        return Result.success(resume);
    }

    @ApiOperation("AI 智能优化简历")
    @PostMapping("/ai-optimize")
    public Result<String> aiOptimize(@RequestBody Map<String, String> params, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Resume resume = resumeService.getResumeByUserId(userId);
        if (resume == null) {
            return Result.error("请先完善简历信息");
        }
        
        String jobDescription = params.get("jobDescription");
        String resumeContent = String.format(
            "个人信息: %s\n技能: %s\n项目: %s\n经历: %s\n自我评价: %s",
            resume.getName(), resume.getSkills(), resume.getProjects(), resume.getExperience(), resume.getSelfEvaluation()
        );
        
        String optimization = aiService.optimizeResume(resumeContent, jobDescription);
        return Result.success(optimization);
    }

    @ApiOperation("保存/更新简历")
    @PostMapping("/save")
    public Result<String> saveResume(@RequestBody Resume resume, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        resume.setUserId(userId);
        // 删除 COS 上的旧简历文件
        if (cosUtil.isEnabled() && resume.getFileUrl() != null) {
            Resume existing = resumeService.getResumeByUserId(userId);
            if (existing != null && existing.getFileUrl() != null
                    && !existing.getFileUrl().equals(resume.getFileUrl())
                    && existing.getFileUrl().startsWith(cosBaseUrl)) {
                String oldKey = existing.getFileUrl().substring(cosBaseUrl.length() + 1);
                cosUtil.delete(oldKey);
            }
        }
        boolean success = resumeService.saveOrUpdateResume(resume);
        return success ? Result.success("保存成功") : Result.error("保存失败");
    }
}
