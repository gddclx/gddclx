package com.example.demo.dto;

import lombok.Data;
import java.util.List;

@Data
public class SubmissionResponse {
    private Long id;
    private Long taskId;
    private String employeeId;
    private String content;
    private String status;
    private String reviewComment;
    private String createdAt;
    private List<TaskResponse.FileInfo> files;
}
