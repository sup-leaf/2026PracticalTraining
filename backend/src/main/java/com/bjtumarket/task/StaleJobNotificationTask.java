package com.bjtumarket.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bjtumarket.entity.Job;
import com.bjtumarket.entity.Notification;
import com.bjtumarket.mapper.NotificationMapper;
import com.bjtumarket.service.JobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * S6.2 — 定时扫描过期岗位并推送通知
 * 扫描超过14天无人投递的岗位，向发布者推送通知
 */
@Slf4j
@Component
public class StaleJobNotificationTask {

    @Autowired
    private JobService jobService;

    @Autowired
    private NotificationMapper notificationMapper;

    /**
     * 每天凌晨2点执行一次
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void scanStaleJobs() {
        log.info("开始扫描过期岗位（超过14天无人投递）...");

        LambdaQueryWrapper<Job> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Job::getStatus, 1)
               .eq(Job::getDeliveryCount, 0)
               .lt(Job::getCreateTime, LocalDateTime.now().minusDays(14));

        List<Job> staleJobs = jobService.list(wrapper);
        log.info("发现 {} 个过期岗位", staleJobs.size());

        int notified = 0;
        for (Job job : staleJobs) {
            try {
                // 检查是否已通知过该岗位
                LambdaQueryWrapper<Notification> check = new LambdaQueryWrapper<>();
                check.eq(Notification::getType, "stale_job")
                      .eq(Notification::getRelatedId, job.getId());
                if (notificationMapper.selectCount(check) > 0) {
                    continue;
                }

                Notification notification = new Notification();
                notification.setUserId(job.getPublisherId());
                notification.setType("stale_job");
                notification.setTitle("岗位超过14天无人投递");
                notification.setContent("您的岗位「" + job.getTitle() + "」已发布超过14天且无人投递，建议更新岗位描述或调整薪资范围。");
                notification.setRelatedId(job.getId());
                notification.setIsRead(0);
                notification.setCreateTime(LocalDateTime.now());
                notificationMapper.insert(notification);
                notified++;
            } catch (Exception e) {
                log.error("处理岗位 {} 通知失败: {}", job.getId(), e.getMessage());
            }
        }

        log.info("过期岗位通知完成，新增 {} 条通知", notified);
    }
}
