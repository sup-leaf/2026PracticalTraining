package com.bjtumarket.controller;

import com.bjtumarket.service.DeliveryService;
import com.bjtumarket.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/delivery")
@CrossOrigin
public class DeliveryController {

    @Autowired
    private DeliveryService deliveryService;

    @PostMapping("/apply")
    public Result<String> apply(@RequestParam Long jobId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        boolean success = deliveryService.apply(jobId, userId);
        if (!success) {
            return Result.error("投递失败或已投递");
        }
        return Result.success("投递成功");
    }

    @GetMapping("/job/{jobId}")
    public Result<Map<String, Object>> getJobDeliveries(
            @PathVariable Long jobId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(deliveryService.getJobDeliveries(jobId, page, size));
    }

    @GetMapping("/my")
    public Result<Map<String, Object>> getMyDeliveries(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(deliveryService.getMyDeliveries(userId, page, size));
    }

    @GetMapping("/publisher")
    public Result<Map<String, Object>> getDeliveriesByPublisher(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(deliveryService.getDeliveriesByPublisher(userId, page, size));
    }

    @PutMapping("/status")
    public Result<String> updateStatus(
            @RequestParam String deliveryId,
            @RequestParam String deliveryStatus,
            @RequestParam(required = false) String note,
            HttpServletRequest request) {
        try {
            Long dId = Long.parseLong(deliveryId);
            Long userId = (Long) request.getAttribute("userId");
            Integer dStatus = Integer.parseInt(deliveryStatus);
            boolean success = deliveryService.updateStatus(dId, userId, dStatus, note);
            if (!success) {
                return Result.error("更新失败");
            }
            return Result.success("更新成功");
        } catch (Exception e) {
            return Result.error("参数错误: " + e.getMessage());
        }
    }
}
