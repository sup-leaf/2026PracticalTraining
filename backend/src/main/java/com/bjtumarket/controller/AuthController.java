package com.bjtumarket.controller;

import com.bjtumarket.entity.User;
import com.bjtumarket.service.UserService;
import com.bjtumarket.vo.LoginRequest;
import com.bjtumarket.vo.RegisterRequest;
import com.bjtumarket.vo.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<User> login(@RequestBody LoginRequest request) {
        User user = userService.login(request.getUsername(), request.getPassword(), request.getUserType());
        if (user == null) {
            return Result.error(401, "用户名或密码错误");
        }
        user.setPassword(null);
        return Result.success(user);
    }

    @PostMapping("/register")
    public Result<String> register(@RequestBody RegisterRequest request) {
        if (request.getUsername() == null || request.getPassword() == null || request.getUserType() == null) {
            return Result.error("用户名、密码、用户类型不能为空");
        }

        User user = new User();
        BeanUtils.copyProperties(request, user);

        Integer userType = request.getUserType();
        if (userType == 1) {
            if (request.getStudentId() == null || request.getCampusCardNo() == null) {
                return Result.error("学生注册需填写学号和校园卡号");
            }
        } else if (userType == 2) {
            if (request.getCompanyName() == null || request.getCompanyCode() == null) {
                return Result.error("企业注册需填写企业名称和统一社会信用代码");
            }
        } else if (userType == 3) {
            if (request.getTeacherNo() == null) {
                return Result.error("教师注册需填写工号");
            }
        }

        boolean success = userService.register(user);
        if (!success) {
            return Result.error("用户名已存在");
        }
        return Result.success("注册成功");
    }
}