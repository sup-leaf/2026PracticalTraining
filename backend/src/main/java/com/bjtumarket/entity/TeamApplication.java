package com.bjtumarket.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_team_application")
public class TeamApplication {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long teamId;
    private Long studentId;
    private Integer status; // 0-待审核 1-已通过 2-已拒绝
    private String note;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
