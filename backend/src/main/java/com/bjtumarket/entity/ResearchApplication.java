package com.bjtumarket.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_research_application")
public class ResearchApplication {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long projectId;
    private Long studentId;
    private Integer status;
    private String note;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
