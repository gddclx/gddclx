package com.example.demo.controller;

import com.example.demo.dto.CozeChatRequest;
import com.example.demo.service.CozeService;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Coze AI 对话接口 Controller
 * 提供前端与Coze AI智能体的对话通道
 * 只暴露一个端点 POST /api/coze/chat
 * Controller不处理任何业务逻辑，纯转发：接收用户问题 → 调用CozeService → 返回AI回复
 *
 * 完整调用链路：
 * 前端输入框 → POST /api/coze/chat → CozeService.chat()
 * → HTTP POST https://api.coze.cn/v3/chat (SSE流式)
 * → Coze Bot 内部调用 hr_data 插件 → GET /api/agent/data → LLM解析 → 自然语言回复
 */
@RestController
@RequestMapping({"/api/coze"})
@CrossOrigin
public class CozeController {
   @Autowired
   private CozeService cozeService;  // Coze AI服务（HTTP SSE流式调用）

   /**
    * 与Coze AI对话
    * POST /api/coze/chat
    * @param request { botId, user, query, conversationId }
    * @return { code: 200, success: true, data: "AI回复的自然语言文本" }
    */
   @PostMapping({"/chat"})
   public Map<String, Object> chat(@RequestBody CozeChatRequest request) {
      Map<String, Object> result = new HashMap();

      try {
         // 转发到Service层处理（HTTP调用Coze API + SSE流式解析）
         String response = this.cozeService.chat(request);
         result.put("code", 200);
         result.put("success", true);
         result.put("data", response);  // 直接返回AI的自然语言文本
      } catch (Exception var4) {
         Exception e = var4;
         result.put("code", 500);
         result.put("success", false);
         result.put("message", e.getMessage());
      }

      return result;
   }
}
