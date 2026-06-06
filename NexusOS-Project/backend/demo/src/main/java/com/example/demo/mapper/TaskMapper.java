package com.example.demo.mapper;

import com.example.demo.domain.Task;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface TaskMapper {

    @Insert("INSERT INTO task (publisher_id, title, content, deadline, status, column_no) VALUES (#{publisherId}, #{title}, #{content}, #{deadline}, 'active', #{columnNo})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Task task);

    @Select("SELECT * FROM task ORDER BY created_at DESC")
    List<Task> findAll();

    @Select("SELECT * FROM task WHERE id = #{id}")
    Task findById(@Param("id") Long id);

    @Select("SELECT * FROM task WHERE status = 'active' ORDER BY created_at DESC")
    List<Task> findActive();

    @Update("UPDATE task SET status = 'closed' WHERE id = #{id}")
    int closeTask(@Param("id") Long id);

    @Select("SELECT * FROM task WHERE status = 'active' AND column_no = #{columnNo} ORDER BY created_at DESC")
    List<Task> findByColumn(@Param("columnNo") Integer columnNo);

    @Select("SELECT * FROM task WHERE column_no = #{columnNo} ORDER BY created_at DESC")
    List<Task> findByColumnAll(@Param("columnNo") Integer columnNo);

    @Select("SELECT * FROM task WHERE column_no IS NULL OR column_no = 0 ORDER BY created_at DESC")
    List<Task> findUncategorized();

    @Delete("DELETE FROM task WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
}
