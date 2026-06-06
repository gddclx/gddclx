package com.example.demo.domain;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PrivateMessage {
    private Long id;
    private String senderId;
    private String receiverId;
    private String title;
    private String content;
    private Integer isRead;
    private LocalDateTime createdAt;
}
