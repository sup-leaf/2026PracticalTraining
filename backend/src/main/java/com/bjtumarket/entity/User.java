package com.bjtumarket.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String password;

    private Integer userType; // 1-学生 2-企业 3-教师

    private String realName;

    private String studentId; // 学号（学生）

    private String campusCardNo; // 校园卡号（学生）

    private String companyName; // 企业名称（企业）

    private String companyCode; // 企业统一社会信用代码（企业）

    private String teacherNo; // 工号（教师）

    private String phone;

    private String email;

    private Integer status; // 0-禁用 1-正常

    private Integer memberLevel; // 0-免费 1-VIP

    private Integer cooperationType; // 1-深度合作 2-校外普通

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}