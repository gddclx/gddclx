package com.example.demo.service;

import com.example.demo.domain.User;
import java.util.List;

/**
 * 用户服务接口（已废弃）
 * 操作废旧的 user 表（非 employee 表）
 * 实现类：UserServiceImpl
 * 功能已被 EmployeeArchiveService 完全覆盖
 */
public interface UserService {
    int addUser(User user);
    int updateUser(User user);
    int deleteUser(Integer id);
    User getUserById(Integer id);
    List<User> getAllUsers();
}
