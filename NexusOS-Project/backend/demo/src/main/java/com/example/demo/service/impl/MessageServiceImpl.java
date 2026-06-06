package com.example.demo.service.impl;

import com.example.demo.domain.PrivateMessage;
import com.example.demo.mapper.PrivateMessageMapper;
import com.example.demo.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private PrivateMessageMapper messageMapper;

    @Override
    public PrivateMessage send(String senderId, String receiverId, String title, String content) {
        PrivateMessage msg = new PrivateMessage();
        msg.setSenderId(senderId);
        msg.setReceiverId(receiverId);
        msg.setTitle(title);
        msg.setContent(content);
        messageMapper.insert(msg);
        return msg;
    }

    @Override
    public List<PrivateMessage> getReceived(String receiverId) {
        return messageMapper.findByReceiver(receiverId);
    }

    @Override
    public List<PrivateMessage> getSent(String senderId) {
        return messageMapper.findBySender(senderId);
    }

    @Override
    public PrivateMessage getMessage(Long id) {
        return messageMapper.findById(id);
    }

    @Override
    public void markRead(Long id) {
        messageMapper.markRead(id);
    }

    @Override
    public int countUnread(String receiverId) {
        return messageMapper.countUnread(receiverId);
    }

    @Override
    public void deleteMessage(Long id) {
        messageMapper.deleteById(id);
    }

    @Override
    public void deleteAllRead(String receiverId) {
        messageMapper.deleteAllRead(receiverId);
    }
}
