package com.bjtumarket.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bjtumarket.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT COUNT(*) FROM t_user WHERE user_type = 1")
    long countStudents();

    @Select("SELECT COUNT(*) FROM t_user WHERE user_type = 2 AND status = 1")
    long countApprovedEnterprises();

    @Select("SELECT COUNT(*) FROM t_user WHERE user_type = 2 AND status = 0")
    long countPendingEnterprises();
}
