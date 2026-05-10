package com.bjtumarket.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_delivery")
public class Delivery {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long jobId;

    private Long resumeId;

    private Long jobPublisherId; // 岗位发布者ID

    private Integer status; // 0-待查看 1-已查看 2-面试中 3-已录用 4-已拒绝

    private String hrNote; // HR备注

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}