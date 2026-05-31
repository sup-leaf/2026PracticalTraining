package com.bjtumarket.controller;

import com.bjtumarket.entity.*;
import com.bjtumarket.service.*;
import com.bjtumarket.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Api(tags = "成长足迹模块")
@RestController
@RequestMapping("/api/profile")
@CrossOrigin
public class ProfileController {

    @Autowired
    private InternshipService internshipService;

    @Autowired
    private ResearchService researchService;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private ResumeService resumeService;

    @ApiOperation("个人成长时间线（实习+科研+竞赛履历）")
    @GetMapping("/timeline")
    public Result<List<Map<String, Object>>> timeline(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<Map<String, Object>> events = new ArrayList<>();

        // 实习记录
        List<Internship> internships = internshipService.getMyInternships(userId);
        for (Internship i : internships) {
            Map<String, Object> e = new LinkedHashMap<>();
            e.put("type", "internship");
            e.put("date", i.getCreateTime());
            e.put("title", i.getCompanyName() + " · " + i.getPosition());
            e.put("detail", "评分: " + (i.getRating() != null ? i.getRating() + "分" : "未评分") + " | " + (i.getReview() != null ? i.getReview() : ""));
            e.put("status", i.getStatus() == 0 ? "进行中" : "已完成");
            events.add(e);
        }

        // 科研申请
        try {
            List<Map<String, Object>> researchApps = researchService.getMyApplications(userId);
            for (Map<String, Object> r : researchApps) {
                Map<String, Object> e = new LinkedHashMap<>();
                e.put("type", "research");
                e.put("date", ((ResearchApplication) r.get("application")).getCreateTime());
                e.put("title", "科研申请: " + r.get("projectTitle"));
                e.put("detail", "状态: " + statusText((Integer)((ResearchApplication) r.get("application")).getStatus()));
                e.put("status", ((ResearchApplication) r.get("application")).getStatus() == 1 ? "已通过" : "申请中");
                events.add(e);
            }
        } catch (Exception ignored) {}

        // 竞赛申请
        try {
            List<Map<String, Object>> compApps = competitionService.getMyApplications(userId);
            for (Map<String, Object> c : compApps) {
                Map<String, Object> e = new LinkedHashMap<>();
                e.put("type", "competition");
                e.put("date", ((TeamApplication) c.get("application")).getCreateTime());
                e.put("title", "竞赛申请: " + c.get("teamTitle"));
                e.put("detail", "状态: " + statusText((Integer)((TeamApplication) c.get("application")).getStatus()));
                e.put("status", ((TeamApplication) c.get("application")).getStatus() == 1 ? "已通过" : "申请中");
                events.add(e);
            }
        } catch (Exception ignored) {}

        events.sort((a, b) -> ((java.time.LocalDateTime) b.get("date")).compareTo((java.time.LocalDateTime) a.get("date")));
        return Result.success(events);
    }

    private String statusText(int s) {
        return s == 1 ? "已通过" : s == 2 ? "已驳回" : "待审核";
    }
}
