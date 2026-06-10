package com.example.demo.domain;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 任务附件实体类 — 对应数据库表 task_file
 * 管理员创建任务时可以上传多个附件（Word/Excel/PDF等）
 * 一个任务(task)可以有多个附件 → 1:N关系
 * 文件物理存储使用UUID重命名防止冲突
 */
@Data
public class TaskFile {
    /** 附件主键ID，自增 */
    private Long id;
    /** 所属任务ID（外键 → task表） */
    private Long taskId;
    /** 用户上传时的原始文件名（如 "需求文档.docx"） */
    private String fileName;
    /** 服务器存储路径（UUID重命名后，如 "tasks/a3f8c2b1.docx"） */
    private String filePath;
    /** 文件大小（字节数） */
    private Long fileSize;
    /** 文件上传时间 */
    private LocalDateTime createdAt;
}
