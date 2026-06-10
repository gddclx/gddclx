package com.example.demo.domain;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 任务实体类 — 对应数据库表 task
 * 管理员发布任务，员工查看并提交
 * 支持三栏看板布局（columnNo = 1/2/3）
 */
@Data
public class Task {
    /** 任务主键ID，自增 */
    private Long id;
    /** 发布者员工工号（管理员） */
    private String publisherId;
    /** 任务标题 */
    private String title;
    /** 任务详细描述/正文 */
    private String content;
    /** 任务截止日期（只有日期，无时分秒） */
    private LocalDate deadline;
    /** 任务状态：active=进行中, closed=已关闭 */
    private String status;
    /** 看板栏位编号：1=第一栏, 2=第二栏, 3=第三栏 */
    private Integer columnNo;
    /** 任务创建时间 */
    private LocalDateTime createdAt;
    /** 任务最后更新时间 */
    private LocalDateTime updatedAt;
}
