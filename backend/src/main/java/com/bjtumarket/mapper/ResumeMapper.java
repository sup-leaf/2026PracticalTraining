package com.bjtumarket.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bjtumarket.entity.Resume;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface ResumeMapper extends BaseMapper<Resume> {

    @Select("SELECT r.major, COUNT(DISTINCT r.user_id) AS studentCount, " +
            "COUNT(DISTINCT CASE WHEN d.status = 3 THEN r.user_id END) AS acceptedCount " +
            "FROM t_resume r LEFT JOIN t_delivery d ON r.id = d.resume_id " +
            "WHERE r.major IS NOT NULL AND r.major != '' " +
            "GROUP BY r.major")
    List<Map<String, Object>> countByMajor();
}
