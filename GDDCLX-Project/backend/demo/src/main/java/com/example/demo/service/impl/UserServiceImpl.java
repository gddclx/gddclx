package com.example.demo.service.impl;

import com.example.demo.domain.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 用户服务实现类（已废弃）
 * 操作废旧的 user 表（非 employee 表）
 * 被 UserController 调用，但前端无任何页面对接
 * 功能已被 EmployeeArchiveServiceImpl 完全覆盖
 * 保留原因：未清理的脚手架遗留代码
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public int addUser(User user) {
        return userMapper.insert(user);
    }

    @Override
    public int updateUser(User user) {
        return userMapper.update(user);
    }

    @Override
    public int deleteUser(Integer id) {  // 注意：使用Integer而非Long（旧表设计）
        return userMapper.delete(id);
    }

    @Override
    public User getUserById(Integer id) {
        return userMapper.findById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userMapper.findAll();
    }
}
