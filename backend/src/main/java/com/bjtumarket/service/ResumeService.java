package com.bjtumarket.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bjtumarket.entity.Resume;

public interface ResumeService extends IService<Resume> {
    Resume getResumeByUserId(Long userId);

    boolean saveOrUpdateResume(Resume resume);
}