package com.example.demo.mapper;

import com.example.demo.domain.PrivateMessage;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface PrivateMessageMapper {

    @Insert("INSERT INTO private_message (sender_id, receiver_id, title, content) VALUES (#{senderId}, #{receiverId}, #{title}, #{content})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(PrivateMessage msg);

    @Select("SELECT * FROM private_message WHERE receiver_id = #{receiverId} ORDER BY created_at DESC")
    List<PrivateMessage> findByReceiver(@Param("receiverId") String receiverId);

    @Select("SELECT * FROM private_message WHERE sender_id = #{senderId} ORDER BY created_at DESC")
    List<PrivateMessage> findBySender(@Param("senderId") String senderId);

    @Select("SELECT * FROM private_message WHERE id = #{id}")
    PrivateMessage findById(@Param("id") Long id);

    @Update("UPDATE private_message SET is_read = 1 WHERE id = #{id}")
    int markRead(@Param("id") Long id);

    @Select("SELECT COUNT(*) FROM private_message WHERE receiver_id = #{receiverId} AND is_read = 0")
    int countUnread(@Param("receiverId") String receiverId);

    @Delete("DELETE FROM private_message WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    @Delete("DELETE FROM private_message WHERE receiver_id = #{receiverId} AND is_read = 1")
    int deleteAllRead(@Param("receiverId") String receiverId);
}
