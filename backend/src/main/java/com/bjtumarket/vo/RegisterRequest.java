package com.bjtumarket.vo;

import lombok.Data;

@Data
public class RegisterRequest {
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
}