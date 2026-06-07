package com.bjtumarket.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bjtumarket.config.HpjPayUtil;
import com.bjtumarket.entity.PaymentOrder;
import com.bjtumarket.entity.User;
import com.bjtumarket.mapper.PaymentOrderMapper;
import com.bjtumarket.mapper.UserMapper;
import com.bjtumarket.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Api(tags = "支付模块")
@RestController
@RequestMapping("/api/payment")
@CrossOrigin
public class PaymentController {

    @Autowired
    private HpjPayUtil hpjPayUtil;

    @Autowired
    private PaymentOrderMapper paymentOrderMapper;

    @Autowired
    private UserMapper userMapper;

    @ApiOperation("创建 VIP 支付订单（金额 0.02 元）")
    @PostMapping("/vip/create")
    public Result<Map<String, Object>> createVipOrder(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");

        LambdaQueryWrapper<PaymentOrder> oldOrders = new LambdaQueryWrapper<>();
        oldOrders.eq(PaymentOrder::getUserId, userId)
                 .eq(PaymentOrder::getFeeType, "vip")
                 .eq(PaymentOrder::getStatus, "pending");
        paymentOrderMapper.delete(oldOrders);

        String orderNo = "PAY" + System.currentTimeMillis()
                + UUID.randomUUID().toString().replace("-", "").substring(0, 6);

        Map<String, Object> payResult = hpjPayUtil.createOrder(orderNo, hpjPayUtil.getVipFee(), "校园集市VIP会员");

        PaymentOrder order = new PaymentOrder();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setAmount(new BigDecimal(hpjPayUtil.getVipFee()));
        order.setFeeType("vip");
        order.setStatus("pending");
        order.setPayUrl(String.valueOf(payResult.getOrDefault("payUrl", "")));
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        paymentOrderMapper.insert(order);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("orderNo", orderNo);
        result.put("amount", hpjPayUtil.getVipFee());
        result.put("mode", payResult.get("mode"));
        result.put("payUrl", payResult.get("payUrl"));
        result.put("qrcode", payResult.get("qrcode"));
        return Result.success(result);
    }

    @ApiOperation("查询支付订单状态")
    @GetMapping("/order/query")
    public Result<Map<String, Object>> queryOrder(@RequestParam String orderNo,
                                                   @RequestParam(required = false) String confirm,
                                                   HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");

        PaymentOrder order = paymentOrderMapper.selectOne(
                new LambdaQueryWrapper<PaymentOrder>().eq(PaymentOrder::getOrderNo, orderNo));
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            return Result.error(403, "无权查看此订单");
        }

        if ("1".equals(confirm)) {
            order.setStatus("success");
            order.setTradeNo("SIMULATE_" + orderNo);
            order.setUpdateTime(LocalDateTime.now());
            paymentOrderMapper.updateById(order);

            User user = userMapper.selectById(userId);
            if (user != null && (user.getMemberLevel() == null || user.getMemberLevel() != 1)) {
                user.setMemberLevel(1);
                userMapper.updateById(user);
            }

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("orderNo", orderNo);
            result.put("status", "success");
            result.put("message", "支付成功，已升级为VIP会员");
            result.put("memberLevel", 1);
            return Result.success(result);
        }

        if ("pending".equals(order.getStatus())) {
            Map<String, Object> queryResult = hpjPayUtil.queryOrder(orderNo);
            if ("success".equals(queryResult.get("status"))) {
                order.setStatus("success");
                order.setTradeNo(String.valueOf(queryResult.getOrDefault("tradeNo", "")));
                order.setUpdateTime(LocalDateTime.now());
                paymentOrderMapper.updateById(order);

                User user = userMapper.selectById(userId);
                if (user != null && (user.getMemberLevel() == null || user.getMemberLevel() != 1)) {
                    user.setMemberLevel(1);
                    userMapper.updateById(user);
                }
            }
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("orderNo", orderNo);
        result.put("status", order.getStatus());
        result.put("tradeNo", order.getTradeNo());
        result.put("amount", order.getAmount());
        result.put("payUrl", order.getPayUrl());

        User user = userMapper.selectById(userId);
        result.put("memberLevel", user != null ? user.getMemberLevel() : 0);

        return Result.success(result);
    }

    @ApiOperation("取消支付订单（仅限 pending 状态）")
    @DeleteMapping("/order/{orderNo}")
    public Result<String> cancelOrder(@PathVariable String orderNo, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");

        PaymentOrder order = paymentOrderMapper.selectOne(
                new LambdaQueryWrapper<PaymentOrder>().eq(PaymentOrder::getOrderNo, orderNo));
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            return Result.error(403, "无权操作此订单");
        }
        if (!"pending".equals(order.getStatus())) {
            return Result.error("订单状态不允许取消");
        }

        paymentOrderMapper.deleteById(order.getId());
        return Result.success("订单已取消");
    }

    @ApiOperation("支付异步回调（虎皮椒通知）")
    @PostMapping("/notify")
    public String notify(@RequestParam Map<String, String> params) {
        System.out.println("[虎皮椒] 收到回调: " + params);
        return "success";
    }
}
