package com.example.demo.mapper;

import com.example.demo.domain.PrivateMessage;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 私信表 MyBatis Mapper接口
 * 全部使用注解方式配置SQL（@Select/@Insert/@Update/@Delete）
 * 不需要XML映射文件，适合简单SQL
 */
@Mapper
public interface PrivateMessageMapper {

    /** 插入新消息 — @Options回填自增ID到 msg.id */
    @Insert("INSERT INTO private_message (sender_id, receiver_id, title, content) VALUES (#{senderId}, #{receiverId}, #{title}, #{content})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(PrivateMessage msg);

    /** 收件箱 — 查收到的消息，按时间倒序 */
    @Select("SELECT * FROM private_message WHERE receiver_id = #{receiverId} ORDER BY created_at DESC")
    List<PrivateMessage> findByReceiver(@Param("receiverId") String receiverId);

    /** 发件箱 — 查发出的消息 */
    @Select("SELECT * FROM private_message WHERE sender_id = #{senderId} ORDER BY created_at DESC")
    List<PrivateMessage> findBySender(@Param("senderId") String senderId);

    /** 消息详情 */
    @Select("SELECT * FROM private_message WHERE id = #{id}")
    PrivateMessage findById(@Param("id") Long id);

    /** 标记已读 */
    @Update("UPDATE private_message SET is_read = 1 WHERE id = #{id}")
    int markRead(@Param("id") Long id);

    /** 未读消息计数 — 用于前端红点提示 */
    @Select("SELECT COUNT(*) FROM private_message WHERE receiver_id = #{receiverId} AND is_read = 0")
    int countUnread(@Param("receiverId") String receiverId);

    /** 删除单条消息 */
    @Delete("DELETE FROM private_message WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    /** 一键清空所有已读消息 */
    @Delete("DELETE FROM private_message WHERE receiver_id = #{receiverId} AND is_read = 1")
    int deleteAllRead(@Param("receiverId") String receiverId);
}
