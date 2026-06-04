package com.bjtumarket.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_crawl_history")
public class CrawlHistory {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String source;

    private Integer status; // 0-进行中, 1-成功, 2-失败

    private Integer jobsFound;

    private Integer jobsAdded;

    private String errorMessage;

    private LocalDateTime startTime;

    private LocalDateTime endTime;
}
