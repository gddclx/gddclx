package com.example.demo.dto;

import lombok.Data;
import java.util.List;

/**
 * 任务详情响应DTO — 后端 → 前端
 * 用于任务列表、任务详情、创建任务等接口的返回值
 * 包含任务基本信息、附件列表和提交人数
 */
@Data
public class TaskResponse {
    private Long id;              // 任务ID
    private String publisherId;   // 发布者工号
    private String title;         // 任务标题
    private String content;       // 任务正文
    private String deadline;      // 截止日期
    private String status;        // 状态：active(进行中) / closed(已关闭)
    private Integer columnNo;     // 看板栏位：1/2/3
    private String createdAt;     // 创建时间
    private List<FileInfo> files; // 任务附件列表
    private int submissionCount;  // 已提交人数

    /**
     * 文件信息内部类 — 附件简要信息
     * 同时被 TaskResponse 和 SubmissionResponse 复用
     */
    @Data
    public static class FileInfo {
        private Long id;        // 附件ID
        private String fileName; // 原始文件名
        private String url;      // 访问URL（/uploads/tasks/UUID.docx）
        private Long fileSize;   // 文件大小（字节）
    }
}
