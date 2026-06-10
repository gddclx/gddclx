package com.example.demo.controller;

import com.example.demo.domain.PrivateMessage;
import com.example.demo.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 私信系统 Controller
 * 接口路径前缀：/api/message
 * 提供消息收发、已读标记、未读计数、删除功能
 *
 * 亮点：抽取 ok() 和 error() 私有方法消除重复代码
 * 每个接口只需1行 return ok(data) 或 return error(msg)，代码量减半
 * 7个端点全部使用 @RequestParam（简单参数）而非 @RequestBody
 */
@RestController
@RequestMapping("/api/message")
@CrossOrigin
public class MessageController {

    @Autowired
    private MessageService messageService;

    /**
     * 构建成功响应 { code:200, success:true, data:... }
     * 私有工具方法，消除每个接口中重复的 result.put() 代码
     */
    private Map<String, Object> ok(Object data) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", 200);
        map.put("success", true);
        map.put("data", data);
        return map;
    }

    /**
     * 构建错误响应 { code:500, success:false, message:"..." }
     */
    private Map<String, Object> error(String msg) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", 500);
        map.put("success", false);
        map.put("message", msg);
        return map;
    }

    /**
     * 发送私信
     * POST /api/message/send?senderId=A001&receiverId=A002&title=你好&content=...
     * @param content 消息正文（可选，默认空串）
     */
    @PostMapping("/send")
    public Map<String, Object> send(
            @RequestParam String senderId,
            @RequestParam String receiverId,
            @RequestParam String title,
            @RequestParam(defaultValue = "") String content) {  // defaultValue="" 表示可选参数
        try {
            PrivateMessage msg = messageService.send(senderId, receiverId, title, content);
            return ok(msg);
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    /**
     * 获取收件箱（收到的消息）
     * GET /api/message/received?employeeId=A001
     */
    @GetMapping("/received")
    public Map<String, Object> received(@RequestParam String employeeId) {
        try {
            List<PrivateMessage> list = messageService.getReceived(employeeId);
            return ok(list);
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    /**
     * 获取发件箱（发出的消息）
     * GET /api/message/sent?employeeId=A001
     */
    @GetMapping("/sent")
    public Map<String, Object> sent(@RequestParam String employeeId) {
        try {
            List<PrivateMessage> list = messageService.getSent(employeeId);
            return ok(list);
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    /**
     * 获取消息详情 — 同时自动标为已读
     * GET /api/message/5
     * @param id 消息ID（@PathVariable）
     */
    @GetMapping("/{id}")
    public Map<String, Object> detail(@PathVariable Long id) {
        try {
            PrivateMessage msg = messageService.getMessage(id);
            if (msg == null) return error("消息不存在");
            messageService.markRead(id);  // 查看即标已读
            return ok(msg);
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    /**
     * 手动标记已读
     * POST /api/message/5/read
     */
    @PostMapping("/{id}/read")
    public Map<String, Object> markRead(@PathVariable Long id) {
        try {
            messageService.markRead(id);
            return ok(null);
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    /**
     * 获取未读消息数量 — 用于前端顶部红点提示
     * GET /api/message/unread-count?employeeId=A001
     */
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

    /**
     * 删除单条消息
     * DELETE /api/message/5（RESTful标准写法）
     */
    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        try {
            messageService.deleteMessage(id);
            return ok(null);
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    /**
     * 一键清空所有已读消息
     * DELETE /api/message/read-all?employeeId=A001
     */
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
