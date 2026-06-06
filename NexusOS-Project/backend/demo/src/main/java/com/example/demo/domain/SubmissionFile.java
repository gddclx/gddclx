package com.example.demo.domain;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SubmissionFile {
    private Long id;
    private Long submissionId;
    private String fileName;
    private String filePath;
    private Long fileSize;
    private LocalDateTime createdAt;
}
