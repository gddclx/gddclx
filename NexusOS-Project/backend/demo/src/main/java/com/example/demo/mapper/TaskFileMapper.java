package com.example.demo.mapper;

import com.example.demo.domain.TaskFile;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface TaskFileMapper {

    @Insert("INSERT INTO task_file (task_id, file_name, file_path, file_size) VALUES (#{taskId}, #{fileName}, #{filePath}, #{fileSize})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TaskFile file);

    @Select("SELECT * FROM task_file WHERE task_id = #{taskId}")
    List<TaskFile> findByTaskId(@Param("taskId") Long taskId);

    @Delete("DELETE FROM task_file WHERE task_id = #{taskId}")
    int deleteByTaskId(@Param("taskId") Long taskId);
}
