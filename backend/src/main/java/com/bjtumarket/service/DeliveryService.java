package com.bjtumarket.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bjtumarket.entity.Delivery;

public interface DeliveryService extends IService<Delivery> {
    boolean apply(Long jobId, Long resumeId);

    Page<Delivery> getJobDeliveries(Long jobId, Integer page, Integer size);

    Page<Delivery> getMyDeliveries(Long resumeId, Integer page, Integer size);

    Page<Delivery> getDeliveriesByPublisher(Long publisherId, Integer page, Integer size);

    boolean updateStatus(Long deliveryId, Long publisherId, Integer deliveryStatus, String note);
}