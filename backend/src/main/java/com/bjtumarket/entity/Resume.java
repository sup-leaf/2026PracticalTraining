package com.bjtumarket.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_resume")
public class Resume {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String name;

    private String gender;

    private Integer age;

    private String phone;

    private String email;

    private String avatar;

    private String major;

    private String grade;

    private Double gpa;

    private String skills;

    private String projects;

    private String awards;

    private String experience;

    private String selfEvaluation;

    private String fileUrl; // 简历文件URL

    private Integer status; // 0-未完善 1-已完善

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}