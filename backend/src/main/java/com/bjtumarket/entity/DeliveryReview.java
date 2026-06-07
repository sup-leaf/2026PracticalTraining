package com.bjtumarket.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_delivery_review")
public class DeliveryReview {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long deliveryId;

    private Long studentId;

    private Integer rating;

    private String review;

    private LocalDateTime createTime;
}
