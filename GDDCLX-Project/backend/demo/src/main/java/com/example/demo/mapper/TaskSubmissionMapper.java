package com.example.demo.mapper;

import com.example.demo.domain.TaskSubmission;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 任务提交表 MyBatis Mapper接口
 * 注解方式配置SQL
 * 状态机：pending(待审核) → approved(通过) / rejected(驳回) → pending(重提)
 */
@Mapper
public interface TaskSubmissionMapper {

    /** 插入提交 — 默认状态 'pending' */
    @Insert("INSERT INTO task_submission (task_id, employee_id, content, status) VALUES (#{taskId}, #{employeeId}, #{content}, 'pending')")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TaskSubmission submission);

    /** 查某任务的所有提交 */
    @Select("SELECT * FROM task_submission WHERE task_id = #{taskId} ORDER BY created_at DESC")
    List<TaskSubmission> findByTaskId(@Param("taskId") Long taskId);

    /** 查某员工的所有提交 */
    @Select("SELECT * FROM task_submission WHERE employee_id = #{employeeId} ORDER BY created_at DESC")
    List<TaskSubmission> findByEmployeeId(@Param("employeeId") String employeeId);

    /** 按ID查提交 */
    @Select("SELECT * FROM task_submission WHERE id = #{id}")
    TaskSubmission findById(@Param("id") Long id);

    /** 审核更新：修改status + review_comment */
    @Update("UPDATE task_submission SET status = #{status}, review_comment = #{reviewComment} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status, @Param("reviewComment") String reviewComment);

    /** 查某人对某任务的提交（用于状态机判断：是否已提交/已通过/已驳回） */
    @Select("SELECT * FROM task_submission WHERE task_id = #{taskId} AND employee_id = #{employeeId} ORDER BY created_at DESC LIMIT 1")
    TaskSubmission findByTaskIdAndEmployeeId(@Param("taskId") Long taskId, @Param("employeeId") String employeeId);

    /** 更新提交内容 → status重置为'pending'（驳回后重提） */
    @Update("UPDATE task_submission SET content = #{content}, status = 'pending', review_comment = NULL WHERE id = #{id}")
    int updateContent(@Param("id") Long id, @Param("content") String content);

    /** 按任务ID删除所有提交（级联删除用） */
    @Delete("DELETE FROM task_submission WHERE task_id = #{taskId}")
    int deleteByTaskId(@Param("taskId") Long taskId);
}
