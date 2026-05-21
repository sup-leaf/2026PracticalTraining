package com.bjtumarket.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bjtumarket.entity.Internship;
import com.bjtumarket.entity.InternshipLog;

import java.util.List;
import java.util.Map;

public interface InternshipService extends IService<Internship> {
    Internship startInternship(Long userId, Long deliveryId);

    List<Internship> getMyInternships(Long userId);

    boolean submitLog(Long internshipId, Long userId, Integer weekNum, String content);

    List<InternshipLog> getLogs(Long internshipId);

    boolean endInternship(Long internshipId, Long userId);

    boolean rateInternship(Long internshipId, Long userId, Integer userType, Integer rating, String review);

    Map<String, Object> getCertificateData(Long internshipId);

    List<Map<String, Object>> getPublisherInternships(Long publisherId);
}
