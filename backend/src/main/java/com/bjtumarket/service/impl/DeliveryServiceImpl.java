package com.bjtumarket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bjtumarket.entity.Delivery;
import com.bjtumarket.entity.Job;
import com.bjtumarket.mapper.DeliveryMapper;
import com.bjtumarket.service.DeliveryService;
import com.bjtumarket.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class DeliveryServiceImpl extends ServiceImpl<DeliveryMapper, Delivery> implements DeliveryService {

    @Autowired
    private JobService jobService;

    @Override
    public boolean apply(Long jobId, Long resumeId) {
        Job job = jobService.getById(jobId);
        if (job == null) {
            return false;
        }

        LambdaQueryWrapper<Delivery> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Delivery::getJobId, jobId).eq(Delivery::getResumeId, resumeId);
        if (this.count(wrapper) > 0) {
            return false;
        }

        Delivery delivery = new Delivery();
        delivery.setJobId(jobId);
        delivery.setResumeId(resumeId);
        delivery.setJobPublisherId(job.getPublisherId());
        delivery.setStatus(0);
        delivery.setCreateTime(LocalDateTime.now());
        delivery.setUpdateTime(LocalDateTime.now());

        job.setDeliveryCount(job.getDeliveryCount() + 1);
        jobService.updateById(job);

        return this.save(delivery);
    }

    @Override
    public Page<Delivery> getJobDeliveries(Long jobId, Integer page, Integer size) {
        Page<Delivery> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Delivery> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Delivery::getJobId, jobId)
                .orderByDesc(Delivery::getCreateTime);
        return this.page(pageParam, wrapper);
    }

    @Override
    public Page<Delivery> getMyDeliveries(Long resumeId, Integer page, Integer size) {
        Page<Delivery> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Delivery> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Delivery::getResumeId, resumeId)
                .orderByDesc(Delivery::getCreateTime);
        return this.page(pageParam, wrapper);
    }

    @Override
    public Page<Delivery> getDeliveriesByPublisher(Long publisherId, Integer page, Integer size) {
        Page<Delivery> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Delivery> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Delivery::getJobPublisherId, publisherId)
                .orderByDesc(Delivery::getCreateTime);
        return this.page(pageParam, wrapper);
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
}