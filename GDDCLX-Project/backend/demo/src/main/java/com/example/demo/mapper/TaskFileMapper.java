package com.example.demo.mapper;

import com.example.demo.domain.TaskFile;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 任务附件表 MyBatis Mapper接口
 * 注解方式配置SQL
 * 一个任务可有多个附件 → task_file.task_id 外键关联 task.id
 */
@Mapper
public interface TaskFileMapper {

    /** 插入附件记录 — @Options回填自增ID */
    @Insert("INSERT INTO task_file (task_id, file_name, file_path, file_size) VALUES (#{taskId}, #{fileName}, #{filePath}, #{fileSize})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TaskFile file);

    /** 查某任务的所有附件 */
    @Select("SELECT * FROM task_file WHERE task_id = #{taskId}")
    List<TaskFile> findByTaskId(@Param("taskId") Long taskId);

    /** 删除某任务的所有附件记录（物理文件由Service层先删） */
    @Delete("DELETE FROM task_file WHERE task_id = #{taskId}")
    int deleteByTaskId(@Param("taskId") Long taskId);
}
