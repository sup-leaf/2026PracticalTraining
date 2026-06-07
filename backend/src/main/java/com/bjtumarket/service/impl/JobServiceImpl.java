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
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JobServiceImpl extends ServiceImpl<JobMapper, Job> implements JobService {

    // 城市名称 → [纬度, 经度]
    private static final Map<String, double[]> CITY_COORDS = new LinkedHashMap<>();
    static {
        CITY_COORDS.put("北京", new double[]{39.9, 116.4});
        CITY_COORDS.put("上海", new double[]{31.2, 121.5});
        CITY_COORDS.put("广州", new double[]{23.1, 113.3});
        CITY_COORDS.put("深圳", new double[]{22.5, 114.1});
        CITY_COORDS.put("杭州", new double[]{30.3, 120.2});
        CITY_COORDS.put("成都", new double[]{30.6, 104.1});
        CITY_COORDS.put("武汉", new double[]{30.6, 114.3});
        CITY_COORDS.put("南京", new double[]{32.1, 118.8});
        CITY_COORDS.put("西安", new double[]{34.3, 108.9});
        CITY_COORDS.put("重庆", new double[]{29.6, 106.5});
        CITY_COORDS.put("天津", new double[]{39.1, 117.2});
        CITY_COORDS.put("苏州", new double[]{31.3, 120.6});
        CITY_COORDS.put("长沙", new double[]{28.2, 113.0});
        CITY_COORDS.put("合肥", new double[]{31.8, 117.2});
        CITY_COORDS.put("郑州", new double[]{34.7, 113.6});
        CITY_COORDS.put("济南", new double[]{36.7, 117.0});
        CITY_COORDS.put("厦门", new double[]{24.5, 118.1});
        CITY_COORDS.put("大连", new double[]{38.9, 121.6});
        CITY_COORDS.put("青岛", new double[]{36.1, 120.4});
        CITY_COORDS.put("福州", new double[]{26.1, 119.3});
        CITY_COORDS.put("远程", new double[]{30.0, 110.0}); // 远程工作默认坐标（中国中心）
    }

    @Override
    public List<Map<String, Object>> getJobMapData() {
        LambdaQueryWrapper<Job> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Job::getStatus, 1);
        List<Job> allJobs = this.list(wrapper);

        // 按城市分组
        Map<String, List<Job>> grouped = new LinkedHashMap<>();
        for (Job job : allJobs) {
            String loc = job.getLocation();
            if (loc == null || loc.isEmpty()) continue;

            // 处理 "上海/北京/深圳" 这种多城市格式
            for (String city : loc.split("[/,、]")) {
                city = city.trim();
                if (city.isEmpty()) continue;
                grouped.computeIfAbsent(city, k -> new ArrayList<>()).add(job);
            }
        }

        // 转为地图数据
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, List<Job>> entry : grouped.entrySet()) {
            String city = entry.getKey();
            double[] coords = CITY_COORDS.getOrDefault(city, null);

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("city", city);
            item.put("lat", coords != null ? coords[0] : null);
            item.put("lng", coords != null ? coords[1] : null);
            item.put("count", entry.getValue().size());
            item.put("jobs", entry.getValue().stream()
                    .limit(5)
                    .map(j -> {
                        Map<String, Object> jm = new LinkedHashMap<>();
                        jm.put("id", j.getId());
                        jm.put("title", j.getTitle());
                        jm.put("jobType", j.getJobType());
                        return jm;
                    })
                    .collect(Collectors.toList()));
            result.add(item);
        }

        return result;
    }

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
    @CacheEvict(value = "adminStats", allEntries = true, beforeInvocation = true)
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