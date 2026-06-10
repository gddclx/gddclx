package com.example.demo.mapper;

import com.example.demo.domain.SubmissionFile;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 提交附件表 MyBatis Mapper接口
 * 注解方式配置SQL
 * 一个提交可有多个附件 → submission_file.submission_id 外键关联 task_submission.id
 */
@Mapper
public interface SubmissionFileMapper {

    /** 插入附件记录 */
    @Insert("INSERT INTO submission_file (submission_id, file_name, file_path, file_size) VALUES (#{submissionId}, #{fileName}, #{filePath}, #{fileSize})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SubmissionFile file);

    /** 查某提交的所有附件 */
    @Select("SELECT * FROM submission_file WHERE submission_id = #{submissionId}")
    List<SubmissionFile> findBySubmissionId(@Param("submissionId") Long submissionId);

    /** 按ID删单个附件（驳回重提时替换附件用） */
    @Delete("DELETE FROM submission_file WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    /** 按提交ID删除所有附件（级联删除用） */
    @Delete("DELETE FROM submission_file WHERE submission_id = #{submissionId}")
    int deleteBySubmissionId(@Param("submissionId") Long submissionId);
}
