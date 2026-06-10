package com.example.demo.service;

import com.example.demo.dto.EmployeeCreateRequest;
import com.example.demo.dto.EmployeeDeleteRequest;
import com.example.demo.dto.EmployeeListResponse;
import com.example.demo.dto.EmployeeUpdateRequest;
import java.util.List;

/**
 * 员工档案管理服务接口 — 管理员CRUD操作
 * 实现类：EmployeeArchiveServiceImpl
 * 使用自定义BusinessException处理业务校验失败
 */
public interface EmployeeArchiveService {
    /** 获取全部员工列表（不含密码） */
    List<EmployeeListResponse> getAllEmployees();

    /**
     * 新增员工 — 密码使用默认值 Changeme_123
     * @throws EmployeeArchiveServiceImpl.BusinessException 工号已存在或必填项为空
     */
    void createEmployee(EmployeeCreateRequest request);

    /**
     * 修改员工 — 支持修改工号（先检查新工号冲突）
     * @throws EmployeeArchiveServiceImpl.BusinessException 员工不存在或工号冲突
     */
    void updateEmployee(EmployeeUpdateRequest request);

    /**
     * 删除员工
     * @throws EmployeeArchiveServiceImpl.BusinessException 员工不存在
     */
    void deleteEmployee(EmployeeDeleteRequest request);
}
