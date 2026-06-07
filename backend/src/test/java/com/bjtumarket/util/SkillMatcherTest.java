package com.bjtumarket.util;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SkillMatcherTest {

    @Test
    void extractSkills_shouldReturnMatchingSkills() {
        String text = "I have experience with Java, Spring Boot, and MySQL";
        Set<String> skills = SkillMatcher.extractSkills(text);
        assertTrue(skills.contains("java"));
        assertTrue(skills.contains("spring"));
        assertTrue(skills.contains("mysql"));
        assertEquals(3, skills.size());
    }

    @Test
    void extractSkills_shouldBeCaseInsensitive() {
        String text = "PYTHON, React, DOCKER";
        Set<String> skills = SkillMatcher.extractSkills(text);
        assertTrue(skills.contains("python"));
        assertTrue(skills.contains("react"));
        assertTrue(skills.contains("docker"));
        assertEquals(3, skills.size());
    }

    @Test
    void extractSkills_shouldReturnEmptySetForNullText() {
        Set<String> skills = SkillMatcher.extractSkills(null);
        assertTrue(skills.isEmpty());
    }

    @Test
    void extractSkills_shouldReturnEmptySetForEmptyText() {
        Set<String> skills = SkillMatcher.extractSkills("");
        assertTrue(skills.isEmpty());
    }

    @Test
    void extractSkills_shouldReturnEmptySetWhenNoSkillsFound() {
        String text = "This text contains no technical skills at all";
        Set<String> skills = SkillMatcher.extractSkills(text);
        assertTrue(skills.isEmpty());
    }

    @Test
    void calculateMatchScore_shouldReturnHighScoreForGoodMatch() {
        String resume = "Java, Spring Boot, MySQL, Redis, Docker";
        String job = "Java developer with Spring Boot and MySQL experience";
        double score = SkillMatcher.calculateMatchScore(resume, job);
        // 3 matching skills out of 3 job skills: (3/3 * 70) + 15 = 85
        assertEquals(85.0, score, 0.01);
    }

    @Test
    void calculateMatchScore_shouldReturnLowScoreForPoorMatch() {
        String resume = "Python, Django";
        String job = "Java developer with Spring Boot";
        double score = SkillMatcher.calculateMatchScore(resume, job);
        // 0 matching skills: 0 + 0 = 0
        assertEquals(0.0, score, 0.01);
    }

    @Test
    void calculateMatchScore_shouldReturnZeroForEmptyJobRequirements() {
        String resume = "Java, Spring Boot";
        String job = "";
        double score = SkillMatcher.calculateMatchScore(resume, job);
        assertEquals(0.0, score, 0.01);
    }

    @Test
    void calculateMatchScore_shouldNotExceed100() {
        String resume = "Java, Spring Boot, MySQL, Redis, Docker, Kubernetes, Python, React, Vue";
        String job = "Java";
        double score = SkillMatcher.calculateMatchScore(resume, job);
        // (1/1 * 70) + 15 = 85
        assertTrue(score <= 100.0);
        assertEquals(85.0, score, 0.01);
    }

    @Test
    void getMatchedSkills_shouldReturnOnlyIntersectingSkills() {
        String resume = "Java, Python, MySQL, Docker";
        String job = "Java developer with Docker and Kubernetes";
        List<String> matched = SkillMatcher.getMatchedSkills(resume, job);
        assertTrue(matched.contains("java"));
        assertTrue(matched.contains("docker"));
        assertFalse(matched.contains("python"));
        assertEquals(2, matched.size());
    }
}
