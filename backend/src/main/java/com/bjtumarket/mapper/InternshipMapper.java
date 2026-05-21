package com.bjtumarket.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bjtumarket.entity.Internship;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface InternshipMapper extends BaseMapper<Internship> {

    @Select("SELECT COUNT(*) FROM t_internship")
    long countAll();

    @Select("SELECT COUNT(*) FROM t_internship WHERE status = 0")
    long countActive();

    @Select("SELECT r.major, COUNT(i.id) AS count, AVG(i.rating) AS avgRating " +
            "FROM t_internship i JOIN t_resume r ON i.resume_id = r.id " +
            "WHERE i.rating IS NOT NULL GROUP BY r.major")
    List<Map<String, Object>> countByMajor();

    @Select("SELECT company_name AS name, COUNT(*) AS count " +
            "FROM t_internship GROUP BY company_name ORDER BY count DESC LIMIT 5")
    List<Map<String, Object>> topCompanies();
}
