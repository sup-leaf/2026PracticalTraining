package com.bjtumarket.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_internship_student_review")
public class InternshipStudentReview {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long internshipId;
    private Long studentId;
    private Integer rating;  // 1-5
    private String review;
    private LocalDateTime createTime;
}
