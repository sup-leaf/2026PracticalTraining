package com.bjtumarket.task;

import com.bjtumarket.entity.CrawlHistory;
import com.bjtumarket.entity.Job;
import com.bjtumarket.service.CrawlHistoryService;
import com.bjtumarket.service.JobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
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

    @Autowired
    private RestTemplate restTemplate;

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
            log.info("请求 V2EX API: {}", url);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> topics = restTemplate.getForObject(url, List.class);

            if (topics == null || topics.isEmpty()) {
                log.warn("V2EX API 返回空数据");
                updateHistory(history, 1, 0, 0, "API 返回空数据，可能该节点下暂无帖子");
                return;
            }

            log.info("V2EX API 返回 {} 条帖子", topics.size());

            int foundCount = topics.size();
            int addedCount = 0;
            for (Map<String, Object> topic : topics) {
                try {
                    // V2EX API 返回的 key 可能是 LinkedHashMap，需要安全获取
                    String title = getStringValue(topic, "title");
                    String content = getStringValue(topic, "content");
                    String topicUrl = getStringValue(topic, "url");

                    if (title == null || title.isEmpty()) {
                        log.debug("跳过空标题帖子");
                        continue;
                    }

                    long existing = jobService.lambdaQuery().eq(Job::getTitle, title).count();
                    if (existing > 0) {
                        log.debug("职位已存在，跳过: {}", title);
                        continue;
                    }

                    Job job = new Job();
                    job.setTitle(title);
                    job.setDescription(content != null ? content : "");
                    job.setRequirement("请点击原链接查看详情: " + (topicUrl != null ? topicUrl : ""));
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
                    log.info("新增职位: {} (第 {} 条)", title, addedCount);
                    if (addedCount >= 10) break;
                } catch (Exception inner) {
                    log.error("处理单条帖子失败: {}", inner.getMessage());
                }
            }
            log.info("V2EX 爬取完成，新增 {} 条岗位", addedCount);
            updateHistory(history, 1, foundCount, addedCount, null);
        } catch (RestClientException e) {
            log.error("V2EX API 请求失败: {}", e.getMessage(), e);
            String errMsg = e.getMessage();
            if (e.getCause() != null) {
                errMsg = e.getCause().getMessage();
            }
            updateHistory(history, 2, 0, 0, "网络请求失败: " + errMsg);
        } catch (Exception e) {
            log.error("V2EX 爬取异常: {}", e.getMessage(), e);
            updateHistory(history, 2, 0, 0, e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    private String getStringValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : null;
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
