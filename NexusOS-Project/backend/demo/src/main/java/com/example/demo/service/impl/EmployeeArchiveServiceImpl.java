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

@Service
public class EmployeeArchiveServiceImpl implements EmployeeArchiveService {
   @Autowired
   private EmployeeMapper employeeMapper;

   @Value("${app.default-password:Changeme_123}")
   private String defaultPassword;
   private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

   public List<EmployeeListResponse> getAllEmployees() {
      List<Employee> employees = this.employeeMapper.selectAll();
      if (employees != null && !employees.isEmpty()) {
         List<EmployeeListResponse> result = new ArrayList();
         Iterator var3 = employees.iterator();

         while(var3.hasNext()) {
            Employee emp = (Employee)var3.next();
            String hireDate = emp.getCreateTime() != null ? DATE_FORMAT.format(emp.getCreateTime()) : "";
            result.add(new EmployeeListResponse(emp.getEmployeeId(), emp.getName(), emp.getDepartment() != null ? emp.getDepartment() : "", emp.getPosition() != null ? emp.getPosition() : "", hireDate, emp.getStatus() != null ? emp.getStatus() : "在职"));
         }

         return result;
      } else {
         return Collections.emptyList();
      }
   }

   public void createEmployee(EmployeeCreateRequest request) {
      if (request.getEmployeeId() != null && !request.getEmployeeId().trim().isEmpty()) {
         if (request.getName() != null && !request.getName().trim().isEmpty()) {
            if (request.getDepartment() != null && !request.getDepartment().trim().isEmpty()) {
               if (request.getPosition() != null && !request.getPosition().trim().isEmpty()) {
                  if (request.getStatus() != null && !request.getStatus().trim().isEmpty()) {
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
                        employee.setRole("employee");
                        employee.setPassword(defaultPassword);
                        employee.setCreateTime(now);
                        employee.setUpdateTime(now);
                        this.employeeMapper.insert(employee);
                     }
                  } else {
                     throw new BusinessException("状态不能为空");
                  }
               } else {
                  throw new BusinessException("岗位不能为空");
               }
            } else {
               throw new BusinessException("部门不能为空");
            }
         } else {
            throw new BusinessException("姓名不能为空");
         }
      } else {
         throw new BusinessException("员工工号不能为空");
      }
   }

   public void updateEmployee(EmployeeUpdateRequest request) {
      if (request.getOriginalEmployeeId() != null && !request.getOriginalEmployeeId().trim().isEmpty()) {
         Employee exist = this.employeeMapper.selectByEmployeeId(request.getOriginalEmployeeId());
         if (exist == null) {
            throw new BusinessException("员工不存在");
         } else if (request.getEmployeeId() != null && !request.getEmployeeId().trim().isEmpty()) {
            Employee employee;
            if (!request.getEmployeeId().equals(request.getOriginalEmployeeId())) {
               employee = this.employeeMapper.selectByEmployeeId(request.getEmployeeId());
               if (employee != null) {
                  throw new BusinessException("工号冲突");
               }
            }

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
                        this.employeeMapper.updateByEmployeeId(request.getOriginalEmployeeId(), employee);
                     } else {
                        throw new BusinessException("状态不能为空");
                     }
                  } else {
                     throw new BusinessException("岗位不能为空");
                  }
               } else {
                  throw new BusinessException("部门不能为空");
               }
            } else {
               throw new BusinessException("姓名不能为空");
            }
         } else {
            throw new BusinessException("员工工号不能为空");
         }
      } else {
         throw new BusinessException("原工号不能为空");
      }
   }

   public void deleteEmployee(EmployeeDeleteRequest request) {
      if (request.getEmployeeId() != null && !request.getEmployeeId().trim().isEmpty()) {
         Employee exist = this.employeeMapper.selectByEmployeeId(request.getEmployeeId());
         if (exist == null) {
            throw new BusinessException("员工不存在");
         } else {
            this.employeeMapper.deleteByEmployeeId(request.getEmployeeId());
         }
      } else {
         throw new BusinessException("员工工号不能为空");
      }
   }

   public static class BusinessException extends RuntimeException {
      public BusinessException(String message) {
         super(message);
      }
   }
}
