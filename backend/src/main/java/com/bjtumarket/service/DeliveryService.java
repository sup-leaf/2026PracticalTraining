package com.bjtumarket.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bjtumarket.entity.Delivery;

import java.util.Map;

public interface DeliveryService extends IService<Delivery> {
    boolean apply(Long jobId, Long userId);

    Map<String, Object> getJobDeliveries(Long jobId, Integer page, Integer size,
                                          Double gpaMin, String major, String skillTag);

    Map<String, Object> getMyDeliveries(Long userId, Integer page, Integer size);

    Map<String, Object> getDeliveriesByPublisher(Long publisherId, Integer page, Integer size,
                                                  Double gpaMin, String major, String skillTag);

    boolean updateStatus(Long deliveryId, Long publisherId, Integer deliveryStatus, String note);
}
