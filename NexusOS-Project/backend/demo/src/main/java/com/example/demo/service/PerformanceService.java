package com.example.demo.service;

import com.example.demo.dto.PerformanceResponse;
import java.util.List;

public interface PerformanceService {
   List<PerformanceResponse> getAllPerformance();

   int confirmLate(String employeeId);

   int confirmEarly(String employeeId);

   int forgiveLate(String employeeId);

   int forgiveEarly(String employeeId);

   void autoMarkEarlyLeave();
}
