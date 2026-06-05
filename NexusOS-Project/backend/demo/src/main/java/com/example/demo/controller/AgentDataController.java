package com.example.demo.controller;

import com.example.demo.domain.Attendance;
import com.example.demo.domain.Employee;
import com.example.demo.mapper.AttendanceMapper;
import com.example.demo.mapper.EmployeeMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/agent"})
@CrossOrigin
public class AgentDataController {
   @Autowired
   private EmployeeMapper employeeMapper;
   @Autowired
   private AttendanceMapper attendanceMapper;

   @GetMapping({"/data"})
   public Map<String, Object> getAllData() {
      Map<String, Object> result = new HashMap();

      try {
         List<Employee> employees = this.employeeMapper.selectAll();
         List<Attendance> attendances = this.attendanceMapper.selectAll();
         long activeEmployees = employees.stream().filter((ex) -> {
            return ex.getStatus() == null || "在职".equals(ex.getStatus()) || "试用".equals(ex.getStatus());
         }).count();
         Map<String, Object> data = new HashMap();
         data.put("totalEmployees", employees.size());
         data.put("activeEmployees", activeEmployees);
         data.put("employees", employees);
         data.put("totalAttendanceRecords", attendances.size());
         data.put("attendances", attendances);
         result.put("code", 200);
         result.put("success", true);
         result.put("message", "success");
         result.put("data", data);
      } catch (Exception var7) {
         Exception e = var7;
         result.put("code", 500);
         result.put("success", false);
         result.put("message", e.getMessage());
         result.put("data", (Object)null);
      }

      return result;
   }
}
