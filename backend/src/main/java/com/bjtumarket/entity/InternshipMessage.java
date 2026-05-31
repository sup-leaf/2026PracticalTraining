package com.bjtumarket.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_internship_message")
public class InternshipMessage {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long internshipId;
    private Long senderId;
    private String content;
    private LocalDateTime createTime;
}
