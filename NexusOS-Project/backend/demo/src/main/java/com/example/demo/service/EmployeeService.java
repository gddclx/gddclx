package com.example.demo.service;

import com.example.demo.domain.Employee;
import com.example.demo.dto.RegisterRequest;

public interface EmployeeService {
   Employee login(String var1, String var2);

   String generateToken(Employee var1);

   boolean register(RegisterRequest var1);
}
