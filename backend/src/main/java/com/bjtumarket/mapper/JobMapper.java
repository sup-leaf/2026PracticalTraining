package com.bjtumarket.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bjtumarket.entity.Job;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface JobMapper extends BaseMapper<Job> {

    @Select("SELECT COUNT(*) FROM t_job WHERE status = 1")
    long countActiveJobs();

    @Select("SELECT u.company_name AS name, COUNT(j.id) AS jobCount, " +
            "(SELECT COUNT(*) FROM t_delivery d WHERE d.job_publisher_id = j.publisher_id) AS applicantCount " +
            "FROM t_job j JOIN t_user u ON j.publisher_id = u.id " +
            "WHERE u.user_type = 2 AND j.status = 1 " +
            "GROUP BY j.publisher_id, u.company_name ORDER BY jobCount DESC LIMIT 5")
    List<Map<String, Object>> topEnterprises();

    @Select("SELECT j.title, u.company_name AS company, COUNT(d.id) AS applicants " +
            "FROM t_delivery d JOIN t_job j ON d.job_id = j.id " +
            "LEFT JOIN t_user u ON j.publisher_id = u.id " +
            "WHERE j.status = 1 " +
            "GROUP BY j.id, j.title, u.company_name ORDER BY applicants DESC LIMIT 5")
    List<Map<String, Object>> hotJobs();

    @Select("SELECT COUNT(*) FROM t_job WHERE publisher_id = #{publisherId}")
    long countJobsByPublisher(Long publisherId);
}
