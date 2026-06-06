package com.example.demo.mapper;

import com.example.demo.domain.SubmissionFile;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface SubmissionFileMapper {

    @Insert("INSERT INTO submission_file (submission_id, file_name, file_path, file_size) VALUES (#{submissionId}, #{fileName}, #{filePath}, #{fileSize})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SubmissionFile file);

    @Select("SELECT * FROM submission_file WHERE submission_id = #{submissionId}")
    List<SubmissionFile> findBySubmissionId(@Param("submissionId") Long submissionId);

    @Delete("DELETE FROM submission_file WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    @Delete("DELETE FROM submission_file WHERE submission_id = #{submissionId}")
    int deleteBySubmissionId(@Param("submissionId") Long submissionId);
}
