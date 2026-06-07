package com.bjtumarket.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bjtumarket.entity.Delivery;
import com.bjtumarket.entity.DeliveryReview;
import com.bjtumarket.mapper.DeliveryReviewMapper;
import com.bjtumarket.service.DeliveryService;
import com.bjtumarket.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Map;

@Api(tags = "投递模块")
@RestController
@RequestMapping("/api/delivery")
@CrossOrigin
public class DeliveryController {

    @Autowired
    private DeliveryService deliveryService;

    @Autowired
    private DeliveryReviewMapper deliveryReviewMapper;

    @ApiOperation("投递岗位")
    @PostMapping("/apply")
    public Result<String> apply(@RequestParam Long jobId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        boolean success = deliveryService.apply(jobId, userId);
        if (!success) {
            return Result.error("投递失败或已投递");
        }
        return Result.success("投递成功");
    }

    @ApiOperation("查看岗位投递列表（含申请人信息+筛选）")
    @GetMapping("/job/{jobId}")
    public Result<Map<String, Object>> getJobDeliveries(
            @PathVariable Long jobId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Double gpaMin,
            @RequestParam(required = false) String major,
            @RequestParam(required = false) String skillTag) {
        return Result.success(deliveryService.getJobDeliveries(jobId, page, size, gpaMin, major, skillTag));
    }

    @ApiOperation("我的投递列表")
    @GetMapping("/my")
    public Result<Map<String, Object>> getMyDeliveries(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(deliveryService.getMyDeliveries(userId, page, size));
    }

    @ApiOperation("我收到的投递（含GPA/专业/技能筛选）")
    @GetMapping("/publisher")
    public Result<Map<String, Object>> getDeliveriesByPublisher(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Double gpaMin,
            @RequestParam(required = false) String major,
            @RequestParam(required = false) String skillTag) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(deliveryService.getDeliveriesByPublisher(userId, page, size, gpaMin, major, skillTag));
    }

    @ApiOperation("更新投递状态（待查看→已查看→面试中→已录用/已拒绝）")
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

    // ==================== S1.2 ====================

    @ApiOperation("学生对面试体验评分（仅已录用或已拒绝的投递）")
    @PostMapping("/rate/interview")
    public Result<String> rateInterview(HttpServletRequest request,
                                         @RequestParam Long deliveryId,
                                         @RequestParam Integer rating,
                                         @RequestParam(required = false) String review) {
        Long userId = (Long) request.getAttribute("userId");
        if (rating < 1 || rating > 5) {
            return Result.error("评分范围为 1-5");
        }

        // 校验投递存在
        Delivery delivery = deliveryService.getById(deliveryId);
        if (delivery == null) {
            return Result.error("投递不存在");
        }

        // 校验是否已评价过
        LambdaQueryWrapper<DeliveryReview> check = new LambdaQueryWrapper<>();
        check.eq(DeliveryReview::getDeliveryId, deliveryId);
        if (deliveryReviewMapper.selectCount(check) > 0) {
            return Result.error("已评价过此投递");
        }

        DeliveryReview dr = new DeliveryReview();
        dr.setDeliveryId(deliveryId);
        dr.setStudentId(userId);
        dr.setRating(rating);
        dr.setReview(review);
        dr.setCreateTime(LocalDateTime.now());
        deliveryReviewMapper.insert(dr);
        return Result.success("评价成功");
    }

    @ApiOperation("查看某投递的面试评价")
    @GetMapping("/rate/interview/{deliveryId}")
    public Result<DeliveryReview> getInterviewRating(@PathVariable Long deliveryId) {
        LambdaQueryWrapper<DeliveryReview> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DeliveryReview::getDeliveryId, deliveryId);
        DeliveryReview dr = deliveryReviewMapper.selectOne(wrapper);
        return dr != null ? Result.success(dr) : Result.error("暂无评价");
    }
}
