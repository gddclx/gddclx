package com.example.demo.controller;

import com.example.demo.domain.Attendance;
import com.example.demo.domain.Employee;
import com.example.demo.dto.PerformanceResponse;
import com.example.demo.dto.PositionStatsResponse;
import com.example.demo.mapper.AttendanceMapper;
import com.example.demo.mapper.EmployeeMapper;
import com.example.demo.service.PerformanceService;
import com.example.demo.service.PositionService;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AI智能体数据接口 Controller
 * 为 Coze AI 机器人提供全量HR数据（员工、今日考勤、绩效、岗位统计）
 * 只暴露一个接口 GET /api/agent/data → Coze工作流通过HTTP插件调用
 * 设计要点：
 * 1. 不返回密码 — 安全
 * 2. 只返回今日考勤 — 避免数据过大超出Coze token限制
 * 3. 用LinkedHashMap保持字段顺序 — 方便Coze LLM解析
 */
@RestController
@RequestMapping({"/api/agent"})
@CrossOrigin
public class AgentDataController {
   @Autowired
   private EmployeeMapper employeeMapper;          // 员工表数据库操作

   @Autowired
   private AttendanceMapper attendanceMapper;      // 考勤表数据库操作

   @Autowired
   private PerformanceService performanceService;  // 绩效计算服务

   @Autowired
   private PositionService positionService;        // 岗位统计服务

   /**
    * 返回全量HR数据供Coze AI智能体解析
    * GET /api/agent/data
    * @return { code, success, data: { company, employees, todayAttendance, performances, positionStats } }
    */
   @GetMapping({"/data"})
   public Map<String, Object> getAllData() {
      Map<String, Object> result = new HashMap();
      SimpleDateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd");        // 日期格式化（只取日期部分）
      SimpleDateFormat timeFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // 时间格式化（完整时间戳）
      String todayStr = dateFmt.format(new Date());  // 今日日期字符串 "2026-06-08"

      try {
         // 第1步：查4个数据源（全量查询，由Coze的LLM自行过滤分析）
         List<Employee> employees = this.employeeMapper.selectAll();         // 所有员工档案
         List<Attendance> allAttendances = this.attendanceMapper.selectAll(); // 所有考勤记录
         List<PerformanceResponse> performances = this.performanceService.getAllPerformance(); // 全员绩效
         List<PositionStatsResponse> positionStats = this.positionService.getPositionStats();   // 岗位统计

         // 第2步：精简约员工列表 — 去掉密码字段，只保留AI需要的关键信息
         List<Map<String, Object>> empList = new ArrayList<>();
         for (Employee e : employees) {
            Map<String, Object> emp = new LinkedHashMap<>();  // LinkedHashMap保持插入顺序
            emp.put("employeeId", e.getEmployeeId());         // 工号
            emp.put("name", e.getName());                     // 姓名
            emp.put("department", e.getDepartment());         // 部门
            emp.put("position", e.getPosition());             // 岗位
            emp.put("status", e.getStatus());                 // 状态
            emp.put("role", e.getRole());                     // 角色
            // 注意：不包含密码 e.getPassword() — 安全设计
            empList.add(emp);
         }

         // 第3步：只保留今日考勤（避免数据过大超过Coze的token限制）
         List<Map<String, Object>> todayAtt = new ArrayList<>();
         for (Attendance a : allAttendances) {
            // 过滤：只取今天日期的记录
            if (dateFmt.format(a.getCheckinDate()).equals(todayStr)) {
               Map<String, Object> att = new LinkedHashMap<>();
               att.put("employeeId", a.getEmployeeId());
               att.put("checkinTime", a.getCheckinTime() != null ? timeFmt.format(a.getCheckinTime()) : null);
               att.put("checkoutTime", a.getCheckoutTime() != null ? timeFmt.format(a.getCheckoutTime()) : null);
               att.put("isLate", a.getIsLate());       // 0=正常, 1=迟到
               att.put("isEarly", a.getIsEarly());     // 0=正常, 1=早退
               att.put("deduct", a.getSalaryDeduct()); // 扣款金额
               todayAtt.add(att);
            }
         }

         // 第4步：统计在职员工数（排除"离职"状态）
         long activeEmployees = employees.stream()
            .filter(ex -> ex.getStatus() == null || "在职".equals(ex.getStatus()) || "试用".equals(ex.getStatus()))
            .count();

         // 第5步：组装完整的嵌套JSON结构返回给Coze
         Map<String, Object> data = new LinkedHashMap<>();
         Map<String, Object> company = new LinkedHashMap<>();
         company.put("totalEmployees", employees.size());     // 总员工数
         company.put("activeEmployees", (int) activeEmployees); // 在职员工数
         company.put("todayChecked", todayAtt.size());        // 今日已打卡人数
         data.put("company", company);
         data.put("employees", empList);          // 员工列表（无密码）
         data.put("todayAttendance", todayAtt);   // 今日考勤
         data.put("performances", performances);  // 绩效数据
         data.put("positionStats", positionStats); // 岗位统计

         result.put("code", 200);
         result.put("success", true);
         result.put("data", data);
      } catch (Exception e) {
         result.put("code", 500);
         result.put("success", false);
         result.put("message", e.getMessage());
      }

      return result;
   }
}
