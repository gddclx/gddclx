package com.example.demo.service.impl;

import com.example.demo.domain.Employee;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.mapper.EmployeeMapper;
import com.example.demo.service.EmployeeService;
import java.util.Base64;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl implements EmployeeService {
   @Autowired
   private EmployeeMapper employeeMapper;

   public Employee login(String employeeId, String password) {
      Employee employee = this.employeeMapper.selectByEmployeeId(employeeId);
      return employee != null && employee.getPassword().equals(password) ? employee : null;
   }

   public String generateToken(Employee employee) {
      String tokenData = employee.getEmployeeId() + ":" + employee.getName() + ":" + System.currentTimeMillis();
      return Base64.getEncoder().encodeToString(tokenData.getBytes());
   }

   public boolean register(RegisterRequest request) {
      if (!request.getPassword().equals(request.getConfirmPassword())) {
         return false;
      } else {
         Employee existEmployee = this.employeeMapper.selectByEmployeeId(request.getEmployeeId());
         if (existEmployee != null) {
            return false;
         } else {
            Employee employee = new Employee();
            employee.setEmployeeId(request.getEmployeeId());
            employee.setPassword(request.getPassword());
            employee.setName(request.getName());
            employee.setRole("employee");
            employee.setDepartment("技术研发");
            employee.setPosition("未分配");
            employee.setStatus("在职");
            employee.setCreateTime(new Date());
            employee.setUpdateTime(new Date());
            int result = this.employeeMapper.insert(employee);
            return result > 0;
         }
      }
   }
}
