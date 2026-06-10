package com.example.demo.service;

import com.example.demo.domain.Employee;
import com.example.demo.dto.RegisterRequest;

/**
 * 员工认证服务接口 — 登录 + 注册 + Token生成
 * 实现类：EmployeeServiceImpl
 */
public interface EmployeeService {
    /**
     * 登录验证 — 明文密码比对
     * @param employeeId 员工工号
     * @param password 明文密码
     * @return 成功返回Employee对象，失败返回null
     */
    Employee login(String employeeId, String password);

    /**
     * 生成简易Token — Base64编码（非标准JWT）
     * 格式：Base64(employeeId:name:timestamp)
     * @param employee 登录成功的员工对象
     * @return Base64编码的Token字符串
     */
    String generateToken(Employee employee);

    /**
     * 用户注册 — 创建新员工记录
     * 所有注册用户初始角色=employee, 部门=技术研发, 岗位=未分配
     * @param request 注册表单（含确认密码）
     * @return true=注册成功, false=工号已存在或密码不一致
     */
    boolean register(RegisterRequest request);
}
