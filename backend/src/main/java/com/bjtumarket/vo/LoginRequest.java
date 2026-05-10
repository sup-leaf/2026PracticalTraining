package com.bjtumarket.vo;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
    private Integer userType; // 1-学生 2-企业 3-教师
}