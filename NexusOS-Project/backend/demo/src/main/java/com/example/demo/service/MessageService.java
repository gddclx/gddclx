package com.example.demo.service;

import com.example.demo.domain.PrivateMessage;
import java.util.List;

public interface MessageService {
    PrivateMessage send(String senderId, String receiverId, String title, String content);
    List<PrivateMessage> getReceived(String receiverId);
    List<PrivateMessage> getSent(String senderId);
    PrivateMessage getMessage(Long id);
    void markRead(Long id);
    int countUnread(String receiverId);
    void deleteMessage(Long id);
    void deleteAllRead(String receiverId);
}
