package com.example.demo.domain;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 任务提交实体类 — 对应数据库表 task_submission
 * 员工对某个任务的提交记录
 * 一个任务(task)可以有多个提交(task_submission) → 1:N关系
 * 状态流转：pending(待审核) → approved(通过) / rejected(驳回)
 *            rejected → pending(重新提交)
 */
@Data
public class TaskSubmission {
    /** 提交记录主键ID，自增 */
    private Long id;
    /** 所属任务ID（外键 → task表） */
    private Long taskId;
    /** 提交者员工工号 */
    private String employeeId;
    /** 提交的文本内容 */
    private String content;
    /** 审核状态：pending=待审核, approved=已通过, rejected=已驳回 */
    private String status;
    /** 管理员审核评语 */
    private String reviewComment;
    /** 提交时间 */
    private LocalDateTime createdAt;
}
