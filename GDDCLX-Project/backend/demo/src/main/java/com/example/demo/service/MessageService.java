package com.example.demo.service;

import com.example.demo.domain.PrivateMessage;
import java.util.List;

/**
 * 私信服务接口
 * 实现类：MessageServiceImpl
 * 所有方法均为Mapper层纯转发，无额外业务逻辑（最"薄"的Service）
 */
public interface MessageService {
    /** 发送私信 — INSERT到数据库 */
    PrivateMessage send(String senderId, String receiverId, String title, String content);

    /** 获取收件箱（收到的消息） */
    List<PrivateMessage> getReceived(String receiverId);

    /** 获取发件箱（发出的消息） */
    List<PrivateMessage> getSent(String senderId);

    /** 查看消息详情 */
    PrivateMessage getMessage(Long id);

    /** 标记消息为已读 */
    void markRead(Long id);

    /** 获取未读消息数量 — 用于前端红点提示 */
    int countUnread(String receiverId);

    /** 删除单条消息 */
    void deleteMessage(Long id);

    /** 一键清空所有已读消息 */
    void deleteAllRead(String receiverId);
}
