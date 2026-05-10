package com.bjtumarket.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bjtumarket.entity.Job;

public interface JobService extends IService<Job> {
    Page<Job> listJobs(Integer page, Integer size, Integer jobType, String keyword);

    Job getJobDetail(Long id);

    boolean publishJob(Job job);

    boolean updateJob(Job job);

    boolean deleteJob(Long id, Long publisherId);

    Page<Job> myJobs(Long publisherId, Integer page, Integer size);
}