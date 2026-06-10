package com.example.demo.service.impl;

import com.example.demo.domain.PrivateMessage;
import com.example.demo.mapper.PrivateMessageMapper;
import com.example.demo.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 私信服务实现类 — 最"薄"的Service
 * 所有方法都是Controller → Mapper的纯转发，零额外业务逻辑
 * 没有@Transactional、没有数据校验、没有数据转换
 * 依赖：PrivateMessageMapper
 */
@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private PrivateMessageMapper messageMapper;

    /** 发送消息 — INSERT后返回含自增ID的完整对象 */
    @Override
    public PrivateMessage send(String senderId, String receiverId, String title, String content) {
        PrivateMessage msg = new PrivateMessage();
        msg.setSenderId(senderId);
        msg.setReceiverId(receiverId);
        msg.setTitle(title);
        msg.setContent(content);
        messageMapper.insert(msg);  // MyBatis自动回填自增ID到msg对象
        return msg;
    }

    /** 收件箱 */
    @Override
    public List<PrivateMessage> getReceived(String receiverId) {
        return messageMapper.findByReceiver(receiverId);
    }

    /** 发件箱 */
    @Override
    public List<PrivateMessage> getSent(String senderId) {
        return messageMapper.findBySender(senderId);
    }

    /** 消息详情 */
    @Override
    public PrivateMessage getMessage(Long id) {
        return messageMapper.findById(id);
    }

    /** 标已读 */
    @Override
    public void markRead(Long id) {
        messageMapper.markRead(id);
    }

    /** 未读数量 */
    @Override
    public int countUnread(String receiverId) {
        return messageMapper.countUnread(receiverId);
    }

    /** 删除单条 */
    @Override
    public void deleteMessage(Long id) {
        messageMapper.deleteById(id);
    }

    /** 一键清空已读 */
    @Override
    public void deleteAllRead(String receiverId) {
        messageMapper.deleteAllRead(receiverId);
    }
}
