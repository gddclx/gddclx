package com.example.demo.service.impl;

import com.example.demo.domain.Employee;
import com.example.demo.dto.EmployeeCreateRequest;
import com.example.demo.dto.EmployeeDeleteRequest;
import com.example.demo.dto.EmployeeListResponse;
import com.example.demo.dto.EmployeeUpdateRequest;
import com.example.demo.mapper.EmployeeMapper;
import com.example.demo.service.EmployeeArchiveService;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 员工档案管理服务实现类
 * 提供管理员对员工的增删改查操作
 * 依赖：EmployeeMapper
 *
 * 设计要点：
 * - 使用自定义BusinessException做业务校验（工号冲突、必填项为空等）
 * - 新增员工使用配置项 app.default-password 作为默认密码
 * - update支持修改工号（先查新工号是否冲突）
 *
 * 槽点：
 * - createEmployee 和 updateEmployee 使用了深层if-else嵌套（箭头代码）
 * - 改进方向：提前返回（early return）或使用 @Valid Bean Validation
 */
@Service
public class EmployeeArchiveServiceImpl implements EmployeeArchiveService {
   @Autowired
   private EmployeeMapper employeeMapper;

   /** 默认密码，从application.properties读取，默认为 Changeme_123 */
   @Value("${app.default-password:Changeme_123}")
   private String defaultPassword;

   private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

   /**
    * 获取全员列表 — 不含密码和薪资敏感字段
    * 返回 EmployeeListResponse 列表，包含工号/姓名/部门/岗位/入职日期/状态
    */
   public List<EmployeeListResponse> getAllEmployees() {
      List<Employee> employees = this.employeeMapper.selectAll();
      if (employees != null && !employees.isEmpty()) {
         List<EmployeeListResponse> result = new ArrayList();
         Iterator var3 = employees.iterator();
         while(var3.hasNext()) {
            Employee emp = (Employee)var3.next();
            String hireDate = emp.getCreateTime() != null ? DATE_FORMAT.format(emp.getCreateTime()) : "";
            // 将Employee转换为EmployeeListResponse（不包含password）
            result.add(new EmployeeListResponse(
               emp.getEmployeeId(), emp.getName(),
               emp.getDepartment() != null ? emp.getDepartment() : "",
               emp.getPosition() != null ? emp.getPosition() : "",
               hireDate,
               emp.getStatus() != null ? emp.getStatus() : "在职"
            ));
         }
         return result;
      } else {
         return Collections.emptyList();  // 无数据返回空列表
      }
   }

   /**
    * 创建员工 — 6层if-else校验（箭头代码反模式）
    * 校验顺序：工号Empty → 姓名Empty → 部门Empty → 岗位Empty → 状态Empty → 工号已存在
    * 通过校验后：设置默认角色(employee)、密码(defaultPassword)、创建时间
    */
   public void createEmployee(EmployeeCreateRequest request) {
      if (request.getEmployeeId() != null && !request.getEmployeeId().trim().isEmpty()) {
         if (request.getName() != null && !request.getName().trim().isEmpty()) {
            if (request.getDepartment() != null && !request.getDepartment().trim().isEmpty()) {
               if (request.getPosition() != null && !request.getPosition().trim().isEmpty()) {
                  if (request.getStatus() != null && !request.getStatus().trim().isEmpty()) {
                     // 检查工号唯一性
                     Employee exist = this.employeeMapper.selectByEmployeeId(request.getEmployeeId());
                     if (exist != null) {
                        throw new BusinessException("工号已存在");
                     } else {
                        Date now = new Date();
                        Employee employee = new Employee();
                        employee.setEmployeeId(request.getEmployeeId());
                        employee.setName(request.getName());
                        employee.setDepartment(request.getDepartment());
                        employee.setPosition(request.getPosition());
                        employee.setStatus(request.getStatus());
                        employee.setRole("employee");          // 固定为普通员工
                        employee.setPassword(defaultPassword);  // 默认密码
                        employee.setCreateTime(now);
                        employee.setUpdateTime(now);
                        this.employeeMapper.insert(employee);
                     }
                  } else { throw new BusinessException("状态不能为空"); }
               } else { throw new BusinessException("岗位不能为空"); }
            } else { throw new BusinessException("部门不能为空"); }
         } else { throw new BusinessException("姓名不能为空"); }
      } else { throw new BusinessException("员工工号不能为空"); }
   }

   /**
    * 修改员工 — 支持修改工号
    * 步骤：查原工号 → 如果工号变了查新工号冲突 → 校验必填项 → UPDATE
    */
   public void updateEmployee(EmployeeUpdateRequest request) {
      if (request.getOriginalEmployeeId() != null && !request.getOriginalEmployeeId().trim().isEmpty()) {
         // 先查原员工是否存在
         Employee exist = this.employeeMapper.selectByEmployeeId(request.getOriginalEmployeeId());
         if (exist == null) {
            throw new BusinessException("员工不存在");
         } else if (request.getEmployeeId() != null && !request.getEmployeeId().trim().isEmpty()) {
            // 如果工号变了，检查新工号是否被占用
            if (!request.getEmployeeId().equals(request.getOriginalEmployeeId())) {
               Employee employee = this.employeeMapper.selectByEmployeeId(request.getEmployeeId());
               if (employee != null) throw new BusinessException("工号冲突");
            }
            // 校验其他必填项...
            if (request.getName() != null && !request.getName().trim().isEmpty()) {
               if (request.getDepartment() != null && !request.getDepartment().trim().isEmpty()) {
                  if (request.getPosition() != null && !request.getPosition().trim().isEmpty()) {
                     if (request.getStatus() != null && !request.getStatus().trim().isEmpty()) {
                        employee = new Employee();
                        employee.setEmployeeId(request.getEmployeeId());
                        employee.setName(request.getName());
                        employee.setDepartment(request.getDepartment());
                        employee.setPosition(request.getPosition());
                        employee.setStatus(request.getStatus());
                        employee.setUpdateTime(new Date());
                        // 用原工号做WHERE条件更新
                        this.employeeMapper.updateByEmployeeId(request.getOriginalEmployeeId(), employee);
                     } else { throw new BusinessException("状态不能为空"); }
                  } else { throw new BusinessException("岗位不能为空"); }
               } else { throw new BusinessException("部门不能为空"); }
            } else { throw new BusinessException("姓名不能为空"); }
         } else { throw new BusinessException("员工工号不能为空"); }
      } else { throw new BusinessException("原工号不能为空"); }
   }

   /**
    * 删除员工 — 先查是否存在再删
    */
   public void deleteEmployee(EmployeeDeleteRequest request) {
      if (request.getEmployeeId() != null && !request.getEmployeeId().trim().isEmpty()) {
         Employee exist = this.employeeMapper.selectByEmployeeId(request.getEmployeeId());
         if (exist == null) {
            throw new BusinessException("员工不存在");
         } else {
            this.employeeMapper.deleteByEmployeeId(request.getEmployeeId());
         }
      } else { throw new BusinessException("员工工号不能为空"); }
   }

   /**
    * 业务异常 — 定义在Service内部的RuntimeException子类
    * Controller中通过 catch(BusinessException) 捕获 → 返回 code:400
    * 使用RuntimeException：不需要在方法签名上声明throws
    */
   public static class BusinessException extends RuntimeException {
      public BusinessException(String message) {
         super(message);
      }
   }
}
