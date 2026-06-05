package com.example.demo.mapper;

import com.example.demo.domain.Employee;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface EmployeeMapper {
   Employee selectByEmployeeId(@Param("employeeId") String var1);

   int insert(Employee var1);

   List<Employee> selectAll();

   int updateByEmployeeId(@Param("originalEmployeeId") String var1, @Param("employee") Employee var2);

   int deleteByEmployeeId(@Param("employeeId") String var1);
}
