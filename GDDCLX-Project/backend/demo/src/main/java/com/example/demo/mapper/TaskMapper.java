package com.example.demo.mapper;

import com.example.demo.domain.Task;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 任务表 MyBatis Mapper接口
 * 全部使用注解方式配置SQL
 */
@Mapper
public interface TaskMapper {

    /** 创建任务 — @Options回填自增ID */
    @Insert("INSERT INTO task (publisher_id, title, content, deadline, status, column_no) VALUES (#{publisherId}, #{title}, #{content}, #{deadline}, 'active', #{columnNo})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Task task);

    /** 全量查询 — 按创建时间倒序 */
    @Select("SELECT * FROM task ORDER BY created_at DESC")
    List<Task> findAll();

    /** 按ID查任务 */
    @Select("SELECT * FROM task WHERE id = #{id}")
    Task findById(@Param("id") Long id);

    /** 查进行中的任务 */
    @Select("SELECT * FROM task WHERE status = 'active' ORDER BY created_at DESC")
    List<Task> findActive();

    /** 关闭任务：status → 'closed' */
    @Update("UPDATE task SET status = 'closed' WHERE id = #{id}")
    int closeTask(@Param("id") Long id);

    /** 按栏位查进行中的任务（三栏看板用） */
    @Select("SELECT * FROM task WHERE status = 'active' AND column_no = #{columnNo} ORDER BY created_at DESC")
    List<Task> findByColumn(@Param("columnNo") Integer columnNo);

    /** 按栏位查所有任务（含已关闭） */
    @Select("SELECT * FROM task WHERE column_no = #{columnNo} ORDER BY created_at DESC")
    List<Task> findByColumnAll(@Param("columnNo") Integer columnNo);

    /** 查未分类任务（column_no IS NULL 或 0） */
    @Select("SELECT * FROM task WHERE column_no IS NULL OR column_no = 0 ORDER BY created_at DESC")
    List<Task> findUncategorized();

    /** 删除任务 */
    @Delete("DELETE FROM task WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
}
