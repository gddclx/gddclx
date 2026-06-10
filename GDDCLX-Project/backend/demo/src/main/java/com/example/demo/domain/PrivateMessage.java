package com.example.demo.domain;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 私信消息实体类 — 对应数据库表 private_message
 * 用于员工之间的一对一私信功能
 * @Data 注解由Lombok在编译时自动生成 getter/setter/toString/equals/hashCode
 */
@Data
public class PrivateMessage {
    /** 消息主键ID，自增 */
    private Long id;
    /** 发送者员工工号（如 A001） */
    private String senderId;
    /** 接收者员工工号（如 A002） */
    private String receiverId;
    /** 消息标题 */
    private String title;
    /** 消息正文内容 */
    private String content;
    /** 是否已读：0=未读, 1=已读 */
    private Integer isRead;
    /** 消息发送时间 */
    private LocalDateTime createdAt;
}
