package com.example.demo.mapper;

import com.example.demo.domain.Employee;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 员工表 MyBatis Mapper接口
 * SQL实现位于 src/main/resources/mapper/EmployeeMapper.xml（XML方式）
 * 提供员工基本CRUD操作
 */
@Mapper
public interface EmployeeMapper {
   /** 按工号查员工（登录、注册查重用） */
   Employee selectByEmployeeId(@Param("employeeId") String var1);

   /** 插入新员工（注册/管理员创建） */
   int insert(Employee var1);

   /** 全量查询（多处使用：列表、绩效、岗位统计） */
   List<Employee> selectAll();

   /** 按原工号更新员工信息（支持修改工号） */
   int updateByEmployeeId(@Param("originalEmployeeId") String var1, @Param("employee") Employee var2);

   /** 按工号删除员工 */
   int deleteByEmployeeId(@Param("employeeId") String var1);
}
