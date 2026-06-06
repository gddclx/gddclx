package com.example.demo.dto;

import lombok.Data;
import java.util.List;

@Data
public class TaskResponse {
    private Long id;
    private String publisherId;
    private String title;
    private String content;
    private String deadline;
    private String status;
    private Integer columnNo;
    private String createdAt;
    private List<FileInfo> files;
    private int submissionCount;

    @Data
    public static class FileInfo {
        private Long id;
        private String fileName;
        private String url;
        private Long fileSize;
    }
}
