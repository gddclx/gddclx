package com.example.demo.domain;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 提交附件实体类 — 对应数据库表 submission_file
 * 员工提交任务时可以上传附件
 * 一个提交(task_submission)可以有多个附件 → 1:N关系
 * 被驳回后重新提交时，旧附件会被物理删除
 */
@Data
public class SubmissionFile {
    /** 附件主键ID，自增 */
    private Long id;
    /** 所属提交记录ID（外键 → task_submission表） */
    private Long submissionId;
    /** 用户上传时的原始文件名 */
    private String fileName;
    /** 服务器存储路径（UUID重命名） */
    private String filePath;
    /** 文件大小（字节数） */
    private Long fileSize;
    /** 上传时间 */
    private LocalDateTime createdAt;
}
