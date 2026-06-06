package com.example.demo.domain;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Task {
    private Long id;
    private String publisherId;
    private String title;
    private String content;
    private LocalDate deadline;
    private String status;
    private Integer columnNo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
