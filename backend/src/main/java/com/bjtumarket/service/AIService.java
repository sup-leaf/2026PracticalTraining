package com.bjtumarket.service;

public interface AIService {
    /**
     * 智能优化简历
     * @param resumeContent 简历原始内容
     * @param jobDescription 目标岗位描述
     * @return 优化后的简历建议
     */
    String optimizeResume(String resumeContent, String jobDescription);
}
