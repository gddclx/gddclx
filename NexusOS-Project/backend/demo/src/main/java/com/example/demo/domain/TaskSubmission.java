package com.example.demo.domain;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TaskSubmission {
    private Long id;
    private Long taskId;
    private String employeeId;
    private String content;
    private String status;
    private String reviewComment;
    private LocalDateTime createdAt;
}
