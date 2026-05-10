package com.bjtumarket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bjtumarket.entity.Resume;
import com.bjtumarket.mapper.ResumeMapper;
import com.bjtumarket.service.ResumeService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ResumeServiceImpl extends ServiceImpl<ResumeMapper, Resume> implements ResumeService {

    @Override
    public Resume getResumeByUserId(Long userId) {
        LambdaQueryWrapper<Resume> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Resume::getUserId, userId);
        return this.getOne(wrapper);
    }

    @Override
    public boolean saveOrUpdateResume(Resume resume) {
        Resume existing = getResumeByUserId(resume.getUserId());
        if (existing != null) {
            resume.setId(existing.getId());
            resume.setUpdateTime(LocalDateTime.now());
        } else {
            resume.setStatus(1);
            resume.setCreateTime(LocalDateTime.now());
            resume.setUpdateTime(LocalDateTime.now());
        }
        return this.saveOrUpdate(resume);
    }
}