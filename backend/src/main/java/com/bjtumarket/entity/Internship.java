package com.bjtumarket.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("t_internship")
public class Internship {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long studentId;

    private Long resumeId;

    private Long jobId;

    private Long deliveryId;

    private String companyName;

    private String position;

    private LocalDate startDate;

    private LocalDate endDate;

    private Integer status; // 0-进行中 1-已完成 2-提前终止

    private Integer rating; // 企业评分 1-5

    private String review; // 企业评价

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
