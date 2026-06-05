package com.example.demo.service;

import com.example.demo.dto.EmployeeCreateRequest;
import com.example.demo.dto.EmployeeDeleteRequest;
import com.example.demo.dto.EmployeeListResponse;
import com.example.demo.dto.EmployeeUpdateRequest;
import java.util.List;

public interface EmployeeArchiveService {
   List<EmployeeListResponse> getAllEmployees();

   void createEmployee(EmployeeCreateRequest var1);

   void updateEmployee(EmployeeUpdateRequest var1);

   void deleteEmployee(EmployeeDeleteRequest var1);
}
