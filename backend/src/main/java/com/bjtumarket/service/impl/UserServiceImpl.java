package com.bjtumarket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bjtumarket.entity.User;
import com.bjtumarket.mapper.UserMapper;
import com.bjtumarket.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public User login(String username, String password, Integer userType) {
        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username)
                .eq(User::getPassword, md5Password)
                .eq(User::getUserType, userType)
                .eq(User::getStatus, 1);
        return this.getOne(wrapper);
    }

    @Override
    public boolean register(User user) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, user.getUsername());
        if (this.count(wrapper) > 0) {
            return false;
        }
        String md5Password = DigestUtils.md5DigestAsHex(user.getPassword().getBytes(StandardCharsets.UTF_8));
        user.setPassword(md5Password);
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        return this.save(user);
    }
}