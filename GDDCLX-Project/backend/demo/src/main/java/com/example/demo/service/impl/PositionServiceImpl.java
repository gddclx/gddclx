package com.example.demo.service.impl;

import com.example.demo.domain.Employee;
import com.example.demo.dto.PositionListPageResponse;
import com.example.demo.dto.PositionListResponse;
import com.example.demo.dto.PositionStatsResponse;
import com.example.demo.dto.PositionUpdateRequest;
import com.example.demo.mapper.EmployeeMapper;
import com.example.demo.service.PositionService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 岗位管理服务实现类
 * 岗位数据存储在 employee.positions JSON列中（非标准范式）
 * 依赖：仅 EmployeeMapper（不依赖独立的岗位表）
 *
 * JSON格式：[{"position":"前端开发","department":"技术研发"}, {"position":"后端开发","department":"技术研发"}]
 *
 * 自定义两个内部异常类：
 * - BusinessException：业务校验失败（400）
 * - NotFoundException：员工不存在（404）
 */
@Service
public class PositionServiceImpl implements PositionService {
   @Autowired
   private EmployeeMapper employeeMapper;
   private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();  // Jackson

   /**
    * 分页岗位列表 — 支持关键词搜索
    * 遍历所有员工 → 解析JSON岗位 → 过滤搜索 → 内存分页（非SQL分页）
    * 槽点：全量加载再分页，员工多时会性能瓶颈
    */
   public PositionListPageResponse getAllPositionList(int page, int pageSize, String search) {
      List<Employee> employees = this.employeeMapper.selectAll();  // 全量加载
      if (employees != null && !employees.isEmpty()) {
         List<PositionListResponse> allList = new ArrayList();

         // 遍历每个员工，解析其JSON岗位列表
         for (Employee emp : employees) {
            List<Map<String, String>> posList = this.parsePositions(emp.getPositions());
            List<String> positionNames = posList.stream()
               .map(p -> p.get("position"))
               .filter(p -> p != null && !p.isEmpty())
               .collect(Collectors.toList());

            String departmentForDisplay = emp.getDepartment() != null ? emp.getDepartment() : "";
            PositionListResponse item = new PositionListResponse(
               emp.getEmployeeId(), emp.getName(), departmentForDisplay, positionNames);

            // 搜索过滤：匹配工号、姓名、岗位名
            if (search == null || search.trim().isEmpty()) {
               allList.add(item);
            } else {
               String keyword = search.trim().toLowerCase();
               boolean match = false;
               if (emp.getEmployeeId() != null && emp.getEmployeeId().toLowerCase().contains(keyword)) match = true;
               if (emp.getName() != null && emp.getName().toLowerCase().contains(keyword)) match = true;
               for (String pn : positionNames) {
                  if (pn.toLowerCase().contains(keyword)) { match = true; break; }
               }
               if (match) allList.add(item);
            }
         }

         // 内存分页
         long total = (long)allList.size();
         int fromIndex = (page - 1) * pageSize;
         if ((long)fromIndex >= total) {
            return new PositionListPageResponse(Collections.emptyList(), total);
         }
         int toIndex = Math.min(fromIndex + pageSize, (int)total);
         List<PositionListResponse> pageList = allList.subList(fromIndex, toIndex);
         return new PositionListPageResponse(pageList, total);
      } else {
         return new PositionListPageResponse(Collections.emptyList(), 0L);
      }
   }

   /**
    * 岗位统计 — 统计每个岗位有多少人
    * 使用 LinkedHashMap 保持插入顺序 + Stream排序（按人数降序）
    */
   public List<PositionStatsResponse> getPositionStats() {
      List<Employee> employees = this.employeeMapper.selectAll();
      Map<String, Integer> countMap = new LinkedHashMap();  // 保序

      for (Employee emp : employees) {
         List<Map<String, String>> posList = this.parsePositions(emp.getPositions());
         for (Map<String, String> pos : posList) {
            String positionName = pos.get("position");
            if (positionName != null && !positionName.isEmpty()) {
               countMap.merge(positionName, 1, Integer::sum);  // 累加计数
            }
         }
      }

      // 按人数降序排序
      return countMap.entrySet().stream()
         .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
         .map(e -> new PositionStatsResponse(e.getKey(), e.getValue()))
         .collect(Collectors.toList());
   }

   /**
    * 更新员工岗位 — Jackson序列化前端数组 → JSON字符串 → 存入数据库
    * 流程：前端传 ["前端","后端"] → 包装为 [{position:"前端",department:""}] → JSON字符串 → UPDATE
    */
   public void updatePositions(PositionUpdateRequest request) {
      if (request.getEmployeeId() != null && !request.getEmployeeId().trim().isEmpty()) {
         // 查员工是否存在
         Employee exist = this.employeeMapper.selectByEmployeeId(request.getEmployeeId());
         if (exist == null) {
            throw new NotFoundException("员工不存在");  // → code:404
         } else {
            // 包装岗位数组为JSON结构
            List<Map<String, String>> posList = new ArrayList<>();
            if (request.getPositions() != null && !request.getPositions().isEmpty()) {
               for (String pos : request.getPositions()) {
                  if (pos != null && !pos.trim().isEmpty()) {
                     Map<String, String> entry = new LinkedHashMap();
                     entry.put("position", pos);
                     entry.put("department", "");
                     posList.add(entry);
                  }
               }
            }

            // 统一填充部门
            String departmentName = request.getDepartment() != null ? request.getDepartment() : "";
            for (Map<String, String> entry : posList) {
               entry.put("department", departmentName);
            }

            // 序列化为JSON字符串
            String pos;
            try {
               pos = OBJECT_MAPPER.writeValueAsString(posList);  // Jackson序列化
               exist.setPositions(pos);
            } catch (Exception var7) {
               throw new RuntimeException("岗位数据序列化失败");
            }

            // 更新部门
            if (request.getDepartment() != null && !request.getDepartment().trim().isEmpty()) {
               exist.setDepartment(request.getDepartment());
            }

            // UPDATE
            Employee updateEmp = new Employee();
            updateEmp.setEmployeeId(exist.getEmployeeId());
            updateEmp.setName(exist.getName());
            updateEmp.setDepartment(exist.getDepartment());
            updateEmp.setStatus(exist.getStatus());
            updateEmp.setPositions(exist.getPositions());
            updateEmp.setUpdateTime(new Date());
            this.employeeMapper.updateByEmployeeId(request.getEmployeeId(), updateEmp);
         }
      } else {
         throw new BusinessException("员工工号不能为空");  // → code:400
      }
   }

   /**
    * 解析 employee.positions JSON列 → List<Map>
    * "[{\"position\":\"前端\",\"department\":\"技术\"}]" → [{position=前端, department=技术}]
    */
   private List<Map<String, String>> parsePositions(String positionsJson) {
      if (positionsJson != null && !positionsJson.trim().isEmpty()) {
         try {
            return OBJECT_MAPPER.readValue(positionsJson,
               new TypeReference<List<Map<String, String>>>() {});
         } catch (Exception var3) {
            return Collections.emptyList();
         }
      } else {
         return Collections.emptyList();
      }
   }

   /** 业务校验异常 → Controller catch → code:400 */
   public static class BusinessException extends RuntimeException {
      public BusinessException(String message) { super(message); }
   }

   /** 资源不存在异常 → Controller catch → code:404 */
   public static class NotFoundException extends RuntimeException {
      public NotFoundException(String message) { super(message); }
   }
}
