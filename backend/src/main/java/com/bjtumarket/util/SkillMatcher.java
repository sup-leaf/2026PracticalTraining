package com.bjtumarket.util;

import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class SkillMatcher {

    // 预定义的技能关键词库（参考 smart_resume）
    private static final List<String> SKILL_KEYWORDS = Arrays.asList(
        // 编程语言
        "python", "java", "javascript", "typescript", "c++", "c#", "go", "rust", "php", "ruby", "swift", "kotlin",
        // 前端技术
        "react", "vue", "angular", "html", "css", "sass", "less", "webpack", "vite", "next.js", "nuxt.js",
        // 后端技术
        "node.js", "express", "django", "flask", "spring", "laravel", "fastapi", "gin", "echo", "mybatis", "springboot",
        // 数据库
        "mysql", "postgresql", "mongodb", "redis", "elasticsearch", "oracle", "sql server",
        // 云服务/运维
        "aws", "azure", "gcp", "aliyun", "docker", "kubernetes", "jenkins", "gitlab", "github", "linux", "nginx",
        // 数据分析/AI
        "pandas", "numpy", "matplotlib", "seaborn", "scikit-learn", "tensorflow", "pytorch", "spark",
        // 其他
        "git", "restful", "graphql", "microservices", "api", "json", "xml"
    );

    /**
     * 从文本中提取技能
     */
    public static Set<String> extractSkills(String text) {
        if (!StringUtils.hasText(text)) {
            return new HashSet<>();
        }
        String lowerText = text.toLowerCase();
        return SKILL_KEYWORDS.stream()
                .filter(skill -> lowerText.contains(skill.toLowerCase()))
                .collect(Collectors.toSet());
    }

    /**
     * 计算简历与岗位的匹配度
     * @param resumeSkills 简历中的技能点（逗号分隔或文本）
     * @param jobRequirements 岗位要求（文本）
     * @return 匹配度得分 (0-100)
     */
    public static double calculateMatchScore(String resumeSkills, String jobRequirements) {
        Set<String> rSkills = extractSkills(resumeSkills);
        Set<String> jSkills = extractSkills(jobRequirements);

        if (jSkills.isEmpty()) {
            return 0.0;
        }

        long matchCount = rSkills.stream()
                .filter(jSkills::contains)
                .count();

        // 基础分：技能匹配占比 70%
        double score = (double) matchCount / jSkills.size() * 70;

        // 加分项：如果简历中包含岗位描述中的其他关键词（如特定的业务领域）
        // 这里简化处理，可以根据需要扩展
        
        return Math.min(100.0, score + (matchCount > 0 ? 15.0 : 0.0)); // 基础匹配奖励 15 分
    }

    /**
     * 获取匹配的技能列表
     */
    public static List<String> getMatchedSkills(String resumeSkills, String jobRequirements) {
        Set<String> rSkills = extractSkills(resumeSkills);
        Set<String> jSkills = extractSkills(jobRequirements);
        
        return rSkills.stream()
                .filter(jSkills::contains)
                .collect(Collectors.toList());
    }
}
