package com.example.demo.mapper;

import com.example.demo.domain.User;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
   int insert(User var1);

   int update(User var1);

   int deleteById(Integer var1);

   User selectById(Integer var1);

   List<User> selectAll();

   User selectByUsername(@Param("username") String var1);
}
