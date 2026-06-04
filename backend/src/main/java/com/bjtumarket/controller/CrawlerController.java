package com.bjtumarket.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bjtumarket.entity.CrawlHistory;
import com.bjtumarket.service.CrawlHistoryService;
import com.bjtumarket.task.JobCrawlerTask;
import com.bjtumarket.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Api(tags = "爬虫管理模块")
@RestController
@RequestMapping("/api/crawler")
@CrossOrigin
public class CrawlerController {

    @Autowired
    private JobCrawlerTask jobCrawlerTask;

    @Autowired
    private CrawlHistoryService crawlHistoryService;

    @ApiOperation("获取爬虫执行历史")
    @GetMapping("/history")
    public Result<Map<String, Object>> getHistory(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {
        Integer userType = (Integer) request.getAttribute("userType");
        if (userType == null || userType != 3) {
            return Result.error("无权访问");
        }
        
        Page<CrawlHistory> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<CrawlHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(CrawlHistory::getStartTime);
        
        Page<CrawlHistory> resultPage = crawlHistoryService.page(pageParam, wrapper);
        
        Map<String, Object> result = new HashMap<>();
        result.put("records", resultPage.getRecords());
        result.put("total", resultPage.getTotal());
        return Result.success(result);
    }

    @ApiOperation("手动触发爬虫")
    @PostMapping("/run")
    public Result<String> runCrawler(HttpServletRequest request) {
        Integer userType = (Integer) request.getAttribute("userType");
        if (userType == null || userType != 3) {
            return Result.error("无权访问");
        }
        
        // 异步运行以避免阻塞
        new Thread(() -> {
            jobCrawlerTask.crawlV2EXJobs();
        }).start();
        return Result.success("爬虫已在后台启动");
    }
}
