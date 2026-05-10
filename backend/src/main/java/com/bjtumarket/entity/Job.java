package com.bjtumarket.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_job")
public class Job {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String description;

    private String requirement;

    private String skillTags;

    private Integer jobType; // 1-实习 2-全职 3-科研助理

    private String location;

    private BigDecimal salaryMin;

    private BigDecimal salaryMax;

    private Integer duration; // 实习时长（月）

    private Long publisherId; // 发布者ID

    private Integer publisherType; // 1-企业 2-教师

    private Integer status; // 0-下架 1-上架

    private Integer viewCount;

    private Integer deliveryCount;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}