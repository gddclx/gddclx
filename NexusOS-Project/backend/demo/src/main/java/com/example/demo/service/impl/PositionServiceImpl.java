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

@Service
public class PositionServiceImpl implements PositionService {
   @Autowired
   private EmployeeMapper employeeMapper;
   private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

   public PositionListPageResponse getAllPositionList(int page, int pageSize, String search) {
      List<Employee> employees = this.employeeMapper.selectAll();
      if (employees != null && !employees.isEmpty()) {
         List<PositionListResponse> allList = new ArrayList();
         Iterator var6 = employees.iterator();

         while(true) {
            PositionListResponse item;
            boolean match;
            do {
               if (!var6.hasNext()) {
                  long total = (long)allList.size();
                  int fromIndex = (page - 1) * pageSize;
                  if ((long)fromIndex >= total) {
                     return new PositionListPageResponse(Collections.emptyList(), total);
                  }

                  int toIndex = Math.min(fromIndex + pageSize, (int)total);
                  List<PositionListResponse> pageList = allList.subList(fromIndex, toIndex);
                  return new PositionListPageResponse(pageList, total);
               }

               Employee emp = (Employee)var6.next();
               List<Map<String, String>> posList = this.parsePositions(emp.getPositions());
               List<String> positionNames = (List)posList.stream().map((p) -> {
                  return (String)p.get("position");
               }).filter((p) -> {
                  return p != null && !p.isEmpty();
               }).collect(Collectors.toList());
               String departmentForDisplay = emp.getDepartment() != null ? emp.getDepartment() : "";
               item = new PositionListResponse(emp.getEmployeeId(), emp.getName(), departmentForDisplay, positionNames);
               if (search == null || search.trim().isEmpty()) {
                  break;
               }

               String keyword = search.trim().toLowerCase();
               match = false;
               if (emp.getEmployeeId() != null && emp.getEmployeeId().toLowerCase().contains(keyword)) {
                  match = true;
               }

               if (emp.getName() != null && emp.getName().toLowerCase().contains(keyword)) {
                  match = true;
               }

               Iterator var14 = positionNames.iterator();

               while(var14.hasNext()) {
                  String pn = (String)var14.next();
                  if (pn.toLowerCase().contains(keyword)) {
                     match = true;
                     break;
                  }
               }
            } while(!match);

            allList.add(item);
         }
      } else {
         return new PositionListPageResponse(Collections.emptyList(), 0L);
      }
   }

   public List<PositionStatsResponse> getPositionStats() {
      List<Employee> employees = this.employeeMapper.selectAll();
      Map<String, Integer> countMap = new LinkedHashMap();
      Iterator var3 = employees.iterator();

      while(var3.hasNext()) {
         Employee emp = (Employee)var3.next();
         List<Map<String, String>> posList = this.parsePositions(emp.getPositions());
         Iterator var6 = posList.iterator();

         while(var6.hasNext()) {
            Map<String, String> pos = (Map)var6.next();
            String positionName = (String)pos.get("position");
            if (positionName != null && !positionName.isEmpty()) {
               countMap.merge(positionName, 1, Integer::sum);
            }
         }
      }

      return (List)countMap.entrySet().stream().sorted((a, b) -> {
         return ((Integer)b.getValue()).compareTo((Integer)a.getValue());
      }).map((e) -> {
         return new PositionStatsResponse((String)e.getKey(), (Integer)e.getValue());
      }).collect(Collectors.toList());
   }

   public void updatePositions(PositionUpdateRequest request) {
      if (request.getEmployeeId() != null && !request.getEmployeeId().trim().isEmpty()) {
         Employee exist = this.employeeMapper.selectByEmployeeId(request.getEmployeeId());
         if (exist == null) {
            throw new NotFoundException("员工不存在");
         } else {
            List<Map<String, String>> posList = new ArrayList();
            String pos;
            if (request.getPositions() != null && !request.getPositions().isEmpty()) {
               Iterator var4 = request.getPositions().iterator();

               while(var4.hasNext()) {
                  pos = (String)var4.next();
                  if (pos != null && !pos.trim().isEmpty()) {
                     Map<String, String> entry = new LinkedHashMap();
                     entry.put("position", pos);
                     entry.put("department", "");
                     posList.add(entry);
                  }
               }
            }

            String departmentName = request.getDepartment() != null ? request.getDepartment() : "";
            Iterator var9 = posList.iterator();

            while(var9.hasNext()) {
               Map<String, String> entry = (Map)var9.next();
               entry.put("department", departmentName);
            }

            try {
               pos = OBJECT_MAPPER.writeValueAsString(posList);
               exist.setPositions(pos);
            } catch (Exception var7) {
               throw new RuntimeException("岗位数据序列化失败");
            }

            if (request.getDepartment() != null && !request.getDepartment().trim().isEmpty()) {
               exist.setDepartment(request.getDepartment());
            }

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
         throw new BusinessException("员工工号不能为空");
      }
   }

   private List<Map<String, String>> parsePositions(String positionsJson) {
      if (positionsJson != null && !positionsJson.trim().isEmpty()) {
         try {
            return (List)OBJECT_MAPPER.readValue(positionsJson, new TypeReference<List<Map<String, String>>>() {
            });
         } catch (Exception var3) {
            return Collections.emptyList();
         }
      } else {
         return Collections.emptyList();
      }
   }

   public static class BusinessException extends RuntimeException {
      public BusinessException(String message) {
         super(message);
      }
   }

   public static class NotFoundException extends RuntimeException {
      public NotFoundException(String message) {
         super(message);
      }
   }
}
