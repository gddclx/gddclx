package com.example.demo.domain;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TaskFile {
    private Long id;
    private Long taskId;
    private String fileName;
    private String filePath;
    private Long fileSize;
    private LocalDateTime createdAt;
}
