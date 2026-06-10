package com.example.game.mapper;

import com.example.game.domain.Employee;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/** 员工表 Mapper（游戏微服务副本，只读查询用） */
@Mapper
public interface EmployeeMapper {
   Employee selectByEmployeeId(@Param("employeeId") String var1);
   int insert(Employee var1);
   List<Employee> selectAll();
   int updateByEmployeeId(@Param("originalEmployeeId") String var1, @Param("employee") Employee var2);
   int deleteByEmployeeId(@Param("employeeId") String var1);
}
