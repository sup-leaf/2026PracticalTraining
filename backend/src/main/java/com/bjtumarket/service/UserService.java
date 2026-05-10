package com.bjtumarket.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bjtumarket.entity.User;

public interface UserService extends IService<User> {
    User login(String username, String password, Integer userType);

    boolean register(User user);
}