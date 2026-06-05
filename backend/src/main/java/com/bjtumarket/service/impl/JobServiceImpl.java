package com.bjtumarket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bjtumarket.entity.Job;
import com.bjtumarket.mapper.JobMapper;
import com.bjtumarket.service.JobService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class JobServiceImpl extends ServiceImpl<JobMapper, Job> implements JobService {

    @Override
    public Page<Job> listJobs(Integer page, Integer size, Integer jobType, String keyword) {
        Page<Job> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Job> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Job::getStatus, 1)
                .orderByDesc(Job::getCreateTime);

        if (jobType != null) {
            wrapper.eq(Job::getJobType, jobType);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Job::getTitle, keyword)
                    .or().like(Job::getDescription, keyword)
                    .or().like(Job::getSkillTags, keyword));
        }

        return this.page(pageParam, wrapper);
    }

    @Override
    public Job getJobDetail(Long id) {
        Job job = this.getById(id);
        if (job != null) {
            job.setViewCount(job.getViewCount() + 1);
            this.updateById(job);
        }
        return job;
    }

    @Override
    @CacheEvict(value = "adminStats", allEntries = true)
    public boolean publishJob(Job job) {
        job.setStatus(1);
        job.setViewCount(0);
        job.setDeliveryCount(0);
        job.setCreateTime(LocalDateTime.now());
        job.setUpdateTime(LocalDateTime.now());
        return this.save(job);
    }

    @Override
    public boolean updateJob(Job job) {
        job.setUpdateTime(LocalDateTime.now());
        return this.updateById(job);
    }

    @Override
    public boolean deleteJob(Long id, Long publisherId) {
        LambdaQueryWrapper<Job> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Job::getId, id).eq(Job::getPublisherId, publisherId);
        return this.remove(wrapper);
    }

    @Override
    public Page<Job> myJobs(Long publisherId, Integer page, Integer size) {
        Page<Job> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Job> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Job::getPublisherId, publisherId)
                .orderByDesc(Job::getCreateTime);
        return this.page(pageParam, wrapper);
    }
}