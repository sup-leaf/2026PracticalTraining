package com.bjtumarket.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_payment_order")
public class PaymentOrder {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;

    private String tradeNo;

    private Long userId;

    private BigDecimal amount;

    private String feeType;

    private String status;

    private String payUrl;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
