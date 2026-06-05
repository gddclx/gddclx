package com.example.demo.service.impl;

import com.example.demo.domain.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.UserService;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
   @Autowired
   private UserMapper userMapper;

   public int addUser(User user) {
      user.setCreateTime(new Date());
      user.setUpdateTime(new Date());
      return this.userMapper.insert(user);
   }

   public int updateUser(User user) {
      user.setUpdateTime(new Date());
      return this.userMapper.update(user);
   }

   public int deleteUser(Integer id) {
      return this.userMapper.deleteById(id);
   }

   public User getUserById(Integer id) {
      return this.userMapper.selectById(id);
   }

   public List<User> getAllUsers() {
      return this.userMapper.selectAll();
   }

   public User getUserByUsername(String username) {
      return this.userMapper.selectByUsername(username);
   }
}
