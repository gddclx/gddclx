package com.example.demo.dto;

import lombok.Data;
import java.util.List;

/**
 * 任务提交响应DTO — 后端 → 前端
 * 用于员工查看自己的提交状态、管理员审核提交
 * 复用了 TaskResponse.FileInfo 内部类作为附件信息
 */
@Data
public class SubmissionResponse {
    private Long id;                     // 提交记录ID
    private Long taskId;                 // 所属任务ID
    private String employeeId;           // 提交员工工号
    private String content;              // 提交文本内容
    private String status;               // 审核状态：pending(待审核) / approved(已通过) / rejected(已驳回)
    private String reviewComment;        // 管理员评语（驳回/通过理由）
    private String createdAt;            // 提交时间
    private List<TaskResponse.FileInfo> files;  // 提交附件列表（复用TaskResponse的FileInfo）
}
