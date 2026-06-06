package com.example.demo.mapper;

import com.example.demo.domain.TaskSubmission;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface TaskSubmissionMapper {

    @Insert("INSERT INTO task_submission (task_id, employee_id, content, status) VALUES (#{taskId}, #{employeeId}, #{content}, 'pending')")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TaskSubmission submission);

    @Select("SELECT * FROM task_submission WHERE task_id = #{taskId} ORDER BY created_at DESC")
    List<TaskSubmission> findByTaskId(@Param("taskId") Long taskId);

    @Select("SELECT * FROM task_submission WHERE employee_id = #{employeeId} ORDER BY created_at DESC")
    List<TaskSubmission> findByEmployeeId(@Param("employeeId") String employeeId);

    @Select("SELECT * FROM task_submission WHERE id = #{id}")
    TaskSubmission findById(@Param("id") Long id);

    @Update("UPDATE task_submission SET status = #{status}, review_comment = #{reviewComment} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status, @Param("reviewComment") String reviewComment);

    @Select("SELECT * FROM task_submission WHERE task_id = #{taskId} AND employee_id = #{employeeId} ORDER BY created_at DESC LIMIT 1")
    TaskSubmission findByTaskIdAndEmployeeId(@Param("taskId") Long taskId, @Param("employeeId") String employeeId);

    @Update("UPDATE task_submission SET content = #{content}, status = 'pending', review_comment = NULL WHERE id = #{id}")
    int updateContent(@Param("id") Long id, @Param("content") String content);

    @Delete("DELETE FROM task_submission WHERE task_id = #{taskId}")
    int deleteByTaskId(@Param("taskId") Long taskId);
}
