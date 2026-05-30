package com.bjtumarket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bjtumarket.entity.*;
import com.bjtumarket.mapper.InternshipLogMapper;
import com.bjtumarket.mapper.InternshipMapper;
import com.bjtumarket.mapper.InternshipStudentReviewMapper;
import com.bjtumarket.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 实习全过程管理服务实现
 * 入职报备、日志提交、企业评分、电子证明、统计
 */
@Service
public class InternshipServiceImpl extends ServiceImpl<InternshipMapper, Internship> implements InternshipService {

    @Autowired
    private InternshipLogMapper logMapper;

    @Autowired
    private InternshipStudentReviewMapper studentReviewMapper;

    @Autowired
    private DeliveryService deliveryService;

    @Autowired
    private JobService jobService;

    @Autowired
    private ResumeService resumeService;

    /**
     * 学生发起实习
     * 条件：投递已被录用(status=3)、简历归属当前用户、当前无进行中的实习
     */
    @Override
    public Internship startInternship(Long userId, Long deliveryId) {
        // 校验投递存在且已被录用
        Delivery delivery = deliveryService.getById(deliveryId);
        if (delivery == null || delivery.getStatus() != 3) {
            return null;
        }
        // 校验投递的简历属于当前学生
        Resume resume = resumeService.getResumeByUserId(userId);
        if (resume == null || !resume.getId().equals(delivery.getResumeId())) {
            return null;
        }

        // 一个学生同时只能有一个进行中的实习
        LambdaQueryWrapper<Internship> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Internship::getStudentId, userId).eq(Internship::getStatus, 0);
        if (this.count(wrapper) > 0) {
            return null;
        }

        // 从投递和岗位信息构造实习记录
        Job job = jobService.getById(delivery.getJobId());
        Internship internship = new Internship();
        internship.setStudentId(userId);
        internship.setResumeId(resume.getId());
        internship.setJobId(delivery.getJobId());
        internship.setDeliveryId(deliveryId);
        internship.setCompanyName(job != null ? job.getTitle() : "");
        internship.setPosition(job != null ? job.getTitle() : "");
        internship.setStartDate(LocalDate.now());
        internship.setStatus(0); // 进行中
        internship.setCreateTime(LocalDateTime.now());
        internship.setUpdateTime(LocalDateTime.now());
        this.save(internship);
        return internship;
    }

    /**
     * 获取学生全部实习记录（按时间倒序）
     */
    @Override
    public List<Internship> getMyInternships(Long userId) {
        LambdaQueryWrapper<Internship> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Internship::getStudentId, userId)
                .orderByDesc(Internship::getCreateTime);
        return this.list(wrapper);
    }

    /**
     * 学生提交实习周日志
     * 限制：只能提交自己实习的日志，且实习必须进行中
     */
    @Override
    public boolean submitLog(Long internshipId, Long userId, Integer weekNum, String content) {
        Internship internship = this.getById(internshipId);
        if (internship == null || !internship.getStudentId().equals(userId)) {
            return false;
        }
        // 只有进行中的实习可以提交日志
        if (internship.getStatus() != 0) {
            return false;
        }
        InternshipLog log = new InternshipLog();
        log.setInternshipId(internshipId);
        log.setWeekNum(weekNum);
        log.setContent(content);
        log.setCreateTime(LocalDateTime.now());
        return logMapper.insert(log) > 0;
    }

    /**
     * 获取某实习的全部日志，按周数升序
     */
    @Override
    public List<InternshipLog> getLogs(Long internshipId) {
        LambdaQueryWrapper<InternshipLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InternshipLog::getInternshipId, internshipId)
                .orderByAsc(InternshipLog::getWeekNum);
        return logMapper.selectList(wrapper);
    }

    /**
     * 学生主动结束实习
     * 设置结束日期、状态改为已完成
     */
    @Override
    public boolean endInternship(Long internshipId, Long userId) {
        Internship internship = this.getById(internshipId);
        if (internship == null || !internship.getStudentId().equals(userId)) {
            return false;
        }
        internship.setStatus(1); // 已完成
        internship.setEndDate(LocalDate.now());
        internship.setUpdateTime(LocalDateTime.now());
        return this.updateById(internship);
    }

    /**
     * 企业对实习评分
     * 权限：企业(userType=2)或教师(userType=3)均可评分
     */
    @Override
    public boolean rateInternship(Long internshipId, Long userId, Integer userType, Integer rating, String review) {
        // 学生不能评分
        if (userType == null || userType == 1) {
            return false;
        }
        Internship internship = this.getById(internshipId);
        if (internship == null) {
            return false;
        }
        internship.setRating(rating);
        internship.setReview(review);
        internship.setUpdateTime(LocalDateTime.now());
        return this.updateById(internship);
    }

    /**
     * 生成实习证明数据
     * 综合实习记录、简历、岗位信息返回结构化 JSON
     */
    @Override
    public Map<String, Object> getCertificateData(Long internshipId) {
        Internship internship = this.getById(internshipId);
        if (internship == null) return Collections.emptyMap();

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("internshipId", internship.getId());

        // 从简历获取学生信息
        Resume resume = resumeService.getById(internship.getResumeId());
        if (resume != null) {
            data.put("studentName", resume.getName());
            data.put("studentMajor", resume.getMajor());
            data.put("studentGrade", resume.getGrade());
        }

        // 从岗位获取企业和职位信息
        Job job = jobService.getById(internship.getJobId());
        if (job != null) {
            data.put("position", job.getTitle());
            data.put("companyName", job.getTitle());
        }

        data.put("startDate", internship.getStartDate());
        data.put("endDate", internship.getEndDate());
        data.put("rating", internship.getRating());
        data.put("review", internship.getReview());
        data.put("status", internship.getStatus());
        return data;
    }

    /**
     * 企业查看自己发布岗位下的所有实习记录
     * 通过 job.publisherId 关联，附带学生姓名和专业
     */
    @Override
    public List<Map<String, Object>> getPublisherInternships(Long publisherId) {
        // 先查该发布者的所有岗位
        LambdaQueryWrapper<Job> jobWrapper = new LambdaQueryWrapper<>();
        jobWrapper.eq(Job::getPublisherId, publisherId);
        List<Job> jobs = jobService.list(jobWrapper);
        if (jobs.isEmpty()) return Collections.emptyList();

        List<Long> jobIds = new ArrayList<>();
        for (Job j : jobs) jobIds.add(j.getId());

        // 再查这些岗位下的所有实习
        LambdaQueryWrapper<Internship> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Internship::getJobId, jobIds).orderByDesc(Internship::getCreateTime);
        List<Internship> list = this.list(wrapper);

        // 富集学生信息
        List<Map<String, Object>> result = new ArrayList<>();
        for (Internship i : list) {
            Map<String, Object> vo = new LinkedHashMap<>();
            vo.put("id", i.getId());
            vo.put("jobId", i.getJobId());
            vo.put("status", i.getStatus());
            vo.put("startDate", i.getStartDate());
            vo.put("rating", i.getRating());
            vo.put("review", i.getReview());

            Resume resume = resumeService.getById(i.getResumeId());
            if (resume != null) {
                vo.put("studentName", resume.getName());
                vo.put("studentMajor", resume.getMajor());
            }

            Job job = jobService.getById(i.getJobId());
            if (job != null) {
                vo.put("position", job.getTitle());
            }

            result.add(vo);
        }
        return result;
    }

    @Override
    public boolean studentReview(Long internshipId, Long studentId, Integer rating, String review) {
        Internship internship = this.getById(internshipId);
        if (internship == null || !internship.getStudentId().equals(studentId)) return false;
        LambdaQueryWrapper<InternshipStudentReview> w = new LambdaQueryWrapper<>();
        w.eq(InternshipStudentReview::getInternshipId, internshipId)
         .eq(InternshipStudentReview::getStudentId, studentId);
        if (studentReviewMapper.selectCount(w) > 0) return false;
        InternshipStudentReview r = new InternshipStudentReview();
        r.setInternshipId(internshipId);
        r.setStudentId(studentId);
        r.setRating(rating);
        r.setReview(review);
        r.setCreateTime(LocalDateTime.now());
        return studentReviewMapper.insert(r) > 0;
    }
}
