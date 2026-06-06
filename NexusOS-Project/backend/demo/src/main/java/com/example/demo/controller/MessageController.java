package com.example.demo.controller;

import com.example.demo.domain.PrivateMessage;
import com.example.demo.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/message")
@CrossOrigin
public class MessageController {

    @Autowired
    private MessageService messageService;

    private Map<String, Object> ok(Object data) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", 200);
        map.put("success", true);
        map.put("data", data);
        return map;
    }

    private Map<String, Object> error(String msg) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", 500);
        map.put("success", false);
        map.put("message", msg);
        return map;
    }

    @PostMapping("/send")
    public Map<String, Object> send(
            @RequestParam String senderId,
            @RequestParam String receiverId,
            @RequestParam String title,
            @RequestParam(defaultValue = "") String content) {
        try {
            PrivateMessage msg = messageService.send(senderId, receiverId, title, content);
            return ok(msg);
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    @GetMapping("/received")
    public Map<String, Object> received(@RequestParam String employeeId) {
        try {
            List<PrivateMessage> list = messageService.getReceived(employeeId);
            return ok(list);
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    @GetMapping("/sent")
    public Map<String, Object> sent(@RequestParam String employeeId) {
        try {
            List<PrivateMessage> list = messageService.getSent(employeeId);
            return ok(list);
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Map<String, Object> detail(@PathVariable Long id) {
        try {
            PrivateMessage msg = messageService.getMessage(id);
            if (msg == null) return error("消息不存在");
            messageService.markRead(id);
            return ok(msg);
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    @PostMapping("/{id}/read")
    public Map<String, Object> markRead(@PathVariable Long id) {
        try {
            messageService.markRead(id);
            return ok(null);
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    @GetMapping("/unread-count")
    public Map<String, Object> unreadCount(@RequestParam String employeeId) {
        try {
            int count = messageService.countUnread(employeeId);
            Map<String, Object> data = new HashMap<>();
            data.put("count", count);
            return ok(data);
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        try {
            messageService.deleteMessage(id);
            return ok(null);
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    @DeleteMapping("/read-all")
    public Map<String, Object> deleteAllRead(@RequestParam String employeeId) {
        try {
            messageService.deleteAllRead(employeeId);
            return ok(null);
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }
}
