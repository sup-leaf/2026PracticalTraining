package com.bjtumarket.task;

import com.bjtumarket.entity.CrawlHistory;
import com.bjtumarket.entity.Job;
import com.bjtumarket.service.CrawlHistoryService;
import com.bjtumarket.service.JobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class JobCrawlerTask {

    @Autowired
    private JobService jobService;

    @Autowired
    private CrawlHistoryService crawlHistoryService;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 每隔 1 小时爬取一次 V2EX 招聘岗位
     */
    @Scheduled(fixedRate = 3600000)
    public void scheduledCrawl() {
        crawlV2EXJobs();
    }

    public void crawlV2EXJobs() {
        log.info("开始爬取 V2EX 招聘信息...");
        CrawlHistory history = new CrawlHistory();
        history.setSource("V2EX");
        history.setStatus(0); // 进行中
        history.setStartTime(LocalDateTime.now());
        crawlHistoryService.save(history);

        try {
            String url = "https://www.v2ex.com/api/topics/show.json?node_name=jobs";
            List<Map<String, Object>> topics = restTemplate.getForObject(url, List.class);

            if (topics == null) {
                updateHistory(history, 1, 0, 0, null);
                return;
            }

            int foundCount = topics.size();
            int addedCount = 0;
            for (Map<String, Object> topic : topics) {
                String title = (String) topic.get("title");
                String content = (String) topic.get("content");
                String topicUrl = (String) topic.get("url");

                long existing = jobService.lambdaQuery().eq(Job::getTitle, title).count();
                if (existing > 0) continue;

                Job job = new Job();
                job.setTitle(title);
                job.setDescription(content);
                job.setRequirement("请点击原链接查看详情: " + topicUrl);
                job.setJobType(2);
                job.setLocation("远程/上海/北京/深圳");
                job.setSalaryMin(new BigDecimal("10000"));
                job.setSalaryMax(new BigDecimal("30000"));
                job.setPublisherId(1L);
                job.setPublisherType(1);
                job.setStatus(1);
                job.setViewCount(0);
                job.setDeliveryCount(0);
                job.setCreateTime(LocalDateTime.now());
                job.setUpdateTime(LocalDateTime.now());

                jobService.save(job);
                addedCount++;
                if (addedCount >= 10) break; // 增加到 10 条
            }
            log.info("V2EX 爬取完成，新增 {} 条岗位", addedCount);
            updateHistory(history, 1, foundCount, addedCount, null);
        } catch (Exception e) {
            log.error("V2EX 爬取失败: {}", e.getMessage());
            updateHistory(history, 2, 0, 0, e.getMessage());
        }
    }

    private void updateHistory(CrawlHistory history, int status, int found, int added, String error) {
        history.setStatus(status);
        history.setJobsFound(found);
        history.setJobsAdded(added);
        history.setErrorMessage(error);
        history.setEndTime(LocalDateTime.now());
        crawlHistoryService.updateById(history);
    }
}
