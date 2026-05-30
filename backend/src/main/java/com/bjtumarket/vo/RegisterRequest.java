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
    private Integer cooperationType; // 合作类型（企业可选）：1深度合作 2校外普通
    private Integer memberLevel; // 会员等级（学生可选）：0免费 1VIP
}