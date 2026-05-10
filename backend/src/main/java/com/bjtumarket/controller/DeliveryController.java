package com.bjtumarket.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bjtumarket.entity.Delivery;
import com.bjtumarket.service.DeliveryService;
import com.bjtumarket.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/delivery")
@CrossOrigin
public class DeliveryController {

    @Autowired
    private DeliveryService deliveryService;

    @PostMapping("/apply")
    public Result<String> apply(@RequestParam Long jobId, @RequestParam Long resumeId) {
        boolean success = deliveryService.apply(jobId, resumeId);
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
        Page<Delivery> pageResult = deliveryService.getJobDeliveries(jobId, page, size);
        Map<String, Object> result = new HashMap<>();
        result.put("records", pageResult.getRecords());
        result.put("total", pageResult.getTotal());
        return Result.success(result);
    }

    @GetMapping("/my")
    public Result<Map<String, Object>> getMyDeliveries(
            @RequestParam Long resumeId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Delivery> pageResult = deliveryService.getMyDeliveries(resumeId, page, size);
        Map<String, Object> result = new HashMap<>();
        result.put("records", pageResult.getRecords());
        result.put("total", pageResult.getTotal());
        return Result.success(result);
    }

    @GetMapping("/publisher")
    public Result<Map<String, Object>> getDeliveriesByPublisher(
            @RequestParam Long publisherId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Delivery> pageResult = deliveryService.getDeliveriesByPublisher(publisherId, page, size);
        Map<String, Object> result = new HashMap<>();
        result.put("records", pageResult.getRecords());
        result.put("total", pageResult.getTotal());
        return Result.success(result);
    }

    @PutMapping("/status")
    public Result<String> updateStatus(
            @RequestParam String deliveryId,
            @RequestParam String publisherId,
            @RequestParam String deliveryStatus,
            @RequestParam(required = false) String note) {
        try {
            Long dId = Long.parseLong(deliveryId);
            Long pId = Long.parseLong(publisherId);
            Integer dStatus = Integer.parseInt(deliveryStatus);
            boolean success = deliveryService.updateStatus(dId, pId, dStatus, note);
            if (!success) {
                return Result.error("更新失败");
            }
            return Result.success("更新成功");
        } catch (Exception e) {
            return Result.error("参数错误: " + e.getMessage());
        }
    }
}