package com.bjtumarket.vo;

import com.bjtumarket.entity.User;
import lombok.Data;

@Data
public class LoginUser {
    private String token;
    private User user;
}
