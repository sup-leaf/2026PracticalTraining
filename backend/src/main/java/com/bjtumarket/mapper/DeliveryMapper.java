package com.bjtumarket.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bjtumarket.entity.Delivery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface DeliveryMapper extends BaseMapper<Delivery> {

    @Select("SELECT COUNT(*) FROM t_delivery WHERE MONTH(create_time) = MONTH(NOW()) AND YEAR(create_time) = YEAR(NOW())")
    long countThisMonth();

    @Select("SELECT DATE(create_time) AS date, COUNT(*) AS count FROM t_delivery " +
            "WHERE create_time >= DATE_SUB(NOW(), INTERVAL 7 DAY) " +
            "GROUP BY DATE(create_time) ORDER BY date")
    List<Map<String, Object>> deliveryTrend();
}
