package com.example.demo.mapper;

import com.example.demo.domain.User;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户表 MyBatis Mapper接口（已废弃）
 * SQL实现位于 src/main/resources/mapper/UserMapper.xml
 * 操作废弃的 user 表（非 employee 表）
 * 前端无任何页面调用，功能被 EmployeeMapper 完全替代
 */
@Mapper
public interface UserMapper {
   int insert(User var1);
   int update(User var1);
   int deleteById(Integer var1);        // 注意：用Integer而非Long（旧表设计）
   User selectById(Integer var1);
   List<User> selectAll();
   User selectByUsername(@Param("username") String var1);  // 按用户名查询
}
