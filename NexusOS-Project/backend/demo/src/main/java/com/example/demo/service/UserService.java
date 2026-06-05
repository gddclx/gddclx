package com.example.demo.service;

import com.example.demo.domain.User;
import java.util.List;

public interface UserService {
   int addUser(User var1);

   int updateUser(User var1);

   int deleteUser(Integer var1);

   User getUserById(Integer var1);

   List<User> getAllUsers();

   User getUserByUsername(String var1);
}
