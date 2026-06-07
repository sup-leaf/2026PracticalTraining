package com.bjtumarket.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bjtumarket.entity.Notification;
import com.bjtumarket.mapper.NotificationMapper;
import com.bjtumarket.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(tags = "通知模块")
@RestController
@RequestMapping("/api/notification")
@CrossOrigin
public class NotificationController {

    @Autowired
    private NotificationMapper notificationMapper;

    @ApiOperation("获取我的未读通知列表")
    @GetMapping("/unread")
    public Result<List<Notification>> getUnread(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getUserId, userId)
               .eq(Notification::getIsRead, 0)
               .orderByDesc(Notification::getCreateTime);
        return Result.success(notificationMapper.selectList(wrapper));
    }

    @ApiOperation("标记通知为已读")
    @PutMapping("/read/{id}")
    public Result<String> markRead(@PathVariable Long id) {
        Notification notification = notificationMapper.selectById(id);
        if (notification == null) return Result.error("通知不存在");
        notification.setIsRead(1);
        notificationMapper.updateById(notification);
        return Result.success("已标记");
    }

    @ApiOperation("获取未读通知数量")
    @GetMapping("/unread/count")
    public Result<Long> unreadCount(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getUserId, userId)
               .eq(Notification::getIsRead, 0);
        return Result.success(notificationMapper.selectCount(wrapper));
    }
}
