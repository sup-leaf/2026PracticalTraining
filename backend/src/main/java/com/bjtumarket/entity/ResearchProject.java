package com.bjtumarket.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_research_project")
public class ResearchProject {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String description;
    private String requirement;
    private String background;
    private String funding;
    private String duration;
    private Long publisherId;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
