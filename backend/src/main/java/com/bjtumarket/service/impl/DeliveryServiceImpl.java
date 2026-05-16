package com.bjtumarket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bjtumarket.entity.Delivery;
import com.bjtumarket.entity.Job;
import com.bjtumarket.entity.Resume;
import com.bjtumarket.mapper.DeliveryMapper;
import com.bjtumarket.service.DeliveryService;
import com.bjtumarket.service.JobService;
import com.bjtumarket.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class DeliveryServiceImpl extends ServiceImpl<DeliveryMapper, Delivery> implements DeliveryService {

    @Autowired
    private JobService jobService;

    @Autowired
    private ResumeService resumeService;

    @Override
    public boolean apply(Long jobId, Long userId) {
        Job job = jobService.getById(jobId);
        if (job == null) {
            return false;
        }
        Resume resume = resumeService.getResumeByUserId(userId);
        if (resume == null) {
            return false;
        }

        LambdaQueryWrapper<Delivery> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Delivery::getJobId, jobId).eq(Delivery::getResumeId, resume.getId());
        if (this.count(wrapper) > 0) {
            return false;
        }

        Delivery delivery = new Delivery();
        delivery.setJobId(jobId);
        delivery.setResumeId(resume.getId());
        delivery.setJobPublisherId(job.getPublisherId());
        delivery.setStatus(0);
        delivery.setCreateTime(LocalDateTime.now());
        delivery.setUpdateTime(LocalDateTime.now());

        job.setDeliveryCount(job.getDeliveryCount() + 1);
        jobService.updateById(job);

        return this.save(delivery);
    }

    @Override
    public Map<String, Object> getJobDeliveries(Long jobId, Integer page, Integer size) {
        Page<Delivery> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Delivery> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Delivery::getJobId, jobId)
                .orderByDesc(Delivery::getCreateTime);
        Page<Delivery> pageResult = this.page(pageParam, wrapper);
        return buildResult(pageResult);
    }

    @Override
    public Map<String, Object> getMyDeliveries(Long userId, Integer page, Integer size) {
        Resume resume = resumeService.getResumeByUserId(userId);
        if (resume == null) {
            Map<String, Object> empty = new HashMap<>();
            empty.put("records", Collections.emptyList());
            empty.put("total", 0L);
            return empty;
        }
        Page<Delivery> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Delivery> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Delivery::getResumeId, resume.getId())
                .orderByDesc(Delivery::getCreateTime);
        Page<Delivery> pageResult = this.page(pageParam, wrapper);
        return buildResult(pageResult);
    }

    @Override
    public Map<String, Object> getDeliveriesByPublisher(Long publisherId, Integer page, Integer size) {
        Page<Delivery> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Delivery> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Delivery::getJobPublisherId, publisherId)
                .orderByDesc(Delivery::getCreateTime);
        Page<Delivery> pageResult = this.page(pageParam, wrapper);
        return buildResult(pageResult);
    }

    @Override
    public boolean updateStatus(Long deliveryId, Long publisherId, Integer deliveryStatus, String note) {
        Delivery delivery = this.getById(deliveryId);
        if (delivery == null) {
            return false;
        }
        if (!delivery.getJobPublisherId().equals(publisherId)) {
            return false;
        }
        delivery.setStatus(deliveryStatus);
        if (StringUtils.hasText(note)) {
            delivery.setHrNote(note);
        }
        delivery.setUpdateTime(LocalDateTime.now());
        return this.updateById(delivery);
    }

    private Map<String, Object> buildResult(Page<Delivery> pageResult) {
        List<Map<String, Object>> records = new ArrayList<>();
        Map<Long, String> jobTitleCache = new HashMap<>();
        Map<Long, Map<String, Object>> resumeCache = new HashMap<>();

        for (Delivery d : pageResult.getRecords()) {
            Map<String, Object> vo = new LinkedHashMap<>();
            vo.put("id", d.getId());
            vo.put("jobId", d.getJobId());
            vo.put("resumeId", d.getResumeId());
            vo.put("jobPublisherId", d.getJobPublisherId());
            vo.put("status", d.getStatus());
            vo.put("hrNote", d.getHrNote());
            vo.put("createTime", d.getCreateTime());
            vo.put("updateTime", d.getUpdateTime());

            String jobTitle = jobTitleCache.computeIfAbsent(d.getJobId(), jid -> {
                Job job = jobService.getById(jid);
                return job != null ? job.getTitle() : "";
            });
            vo.put("jobTitle", jobTitle);

            Map<String, Object> resumeInfo = resumeCache.computeIfAbsent(d.getResumeId(), rid -> {
                Resume r = resumeService.getById(rid);
                if (r == null) return Collections.emptyMap();
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("studentName", r.getName());
                m.put("studentMajor", r.getMajor());
                m.put("studentPhone", r.getPhone());
                m.put("studentEmail", r.getEmail());
                m.put("studentSkills", r.getSkills());
                m.put("studentAwards", r.getAwards());
                m.put("studentGrade", r.getGrade());
                m.put("studentFileUrl", r.getFileUrl());
                return m;
            });
            vo.putAll(resumeInfo);

            records.add(vo);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("records", records);
        result.put("total", pageResult.getTotal());
        return result;
    }
}
