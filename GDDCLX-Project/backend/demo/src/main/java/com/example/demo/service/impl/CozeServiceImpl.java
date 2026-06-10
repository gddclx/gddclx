package com.example.demo.service.impl;

import com.example.demo.dto.CozeChatRequest;
import com.example.demo.service.CozeService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Coze AI 对话服务实现类 — 唯一不用MyBatis的Service
 * 使用原生 HttpURLConnection 调用 Coze API v3，接收SSE流式响应
 * 不注入任何Mapper，纯HTTP客户端
 *
 * 调用流程：
 * 1. POST https://api.coze.cn/v3/chat（Bearer Token认证）
 * 2. 请求体：{ stream:true, bot_id, user_id, additional_messages }
 * 3. 接收SSE流式响应，逐行解析
 * 4. 只提取 conversation.message.delta 事件中的 content 文本
 * 5. 拼接所有文本片段 → 返回完整AI回复
 *
 * SSE事件类型：
 * - conversation.message.delta  → AI回复的文本片段
 * - conversation.chat.completed → 对话完成信号
 *
 * 认证：使用 Coze Personal Access Token (PAT)，格式 pat_xxx
 * Token从环境变量/配置文件注入：@Value("${coze.api.token}")
 */
@Service
public class CozeServiceImpl implements CozeService {
   private static final String COZE_URL = "https://api.coze.cn/v3/chat";  // Coze API地址

   @Value("${coze.api.token:}")                         // Coze PAT令牌
   private String apiToken;

   @Value("${coze.bot.id:7645638589472276518}")         // Coze Bot ID，有默认值
   private String botId;

   private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();  // Jackson JSON解析

   /**
    * 发送对话请求并解析SSE流式响应
    * @param request 前端传来的对话请求
    * @return AI回复的自然语言文本
    * @throws RuntimeException Coze API调用失败或解析失败
    */
   public String chat(CozeChatRequest request) {
      HttpURLConnection conn = null;
      String line;

      try {
         // 第1步：建立HTTP连接
         URL url = new URL(COZE_URL);
         conn = (HttpURLConnection)url.openConnection();
         conn.setRequestMethod("POST");
         conn.setRequestProperty("Authorization", "Bearer " + this.apiToken);  // Bearer Token认证
         conn.setRequestProperty("Content-Type", "application/json");
         conn.setDoOutput(true);
         conn.setConnectTimeout(30000);   // 连接超时30秒
         conn.setReadTimeout(120000);     // 读取超时120秒（AI回复可能较慢）

         // 第2步：构造JSON请求体
         String body = this.buildRequestBody(request);
         System.out.println("=== Coze请求体 ===");  // 调试日志
         System.out.println(body);

         // 第3步：发送请求体
         OutputStream os = conn.getOutputStream();
         try {
            os.write(body.getBytes(StandardCharsets.UTF_8));
            os.flush();
         } finally {
            if (os != null) os.close();
         }

         // 第4步：检查HTTP响应码
         int responseCode = conn.getResponseCode();
         if (responseCode != 200) {
            throw new RuntimeException("Coze API returned " + responseCode);
         }

         // 第5步：逐行读取SSE流式响应
         StringBuilder answerBuilder = new StringBuilder();
         String currentEvent = "";  // 当前事件类型

         BufferedReader reader = new BufferedReader(
            new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
         try {
            while (true) {
               // 跳过空行
               do { line = reader.readLine(); } while (line != null && line.trim().isEmpty());
               if (line == null) break;  // 流结束

               System.out.println("原始数据: " + line);  // 调试日志

               // 解析SSE行
               if (line.startsWith("event:")) {
                  // 事件类型行：event: conversation.message.delta
                  currentEvent = line.substring(6).trim();
               } else {
                  String jsonStr = line;
                  if (jsonStr.startsWith("data:")) {
                     jsonStr = jsonStr.substring(5).trim();  // 去掉 "data:" 前缀
                     if (!jsonStr.isEmpty() && !"[DONE]".equals(jsonStr)) {
                        JsonNode event;
                        try {
                           event = OBJECT_MAPPER.readTree(jsonStr);  // Jackson解析JSON
                        } catch (Exception e) {
                           System.out.println("JSON解析失败: " + e.getMessage());
                           continue;  // 跳过无法解析的行
                        }

                        // 只收集消息增量事件中的文本内容
                        if ("conversation.message.delta".equals(currentEvent)) {
                           String content = event.path("content").asText();
                           if (content != null && !content.isEmpty()) {
                              answerBuilder.append(content);  // 拼接AI回复的每个片段
                           }
                        } else if ("conversation.chat.completed".equals(currentEvent)) {
                           System.out.println("对话完成");
                           break;  // 对话完成，退出循环
                        }
                     }
                  }
               }
            }
         } finally {
            reader.close();
         }

         // 第6步：返回完整AI回复
         String answer = answerBuilder.toString();
         if (!answer.isEmpty()) {
            return answer;
         }
         return "智能体未返回有效回答";

      } catch (RuntimeException e) {
         throw e;  // 保持原异常不包装
      } catch (Exception e) {
         throw new RuntimeException("调用Coze失败: " + e.getMessage(), e);
      } finally {
         if (conn != null) conn.disconnect();  // 断开连接
      }
   }

   /**
    * 手拼JSON请求体（没有用Jackson序列化，更灵活但容易出错）
    * 格式：{"stream":true,"bot_id":"764...","user_id":"A001",
    *        "additional_messages":[{"role":"user","content":"今天考勤？","content_type":"text"}]}
    */
   private String buildRequestBody(CozeChatRequest request) {
      StringBuilder sb = new StringBuilder();
      sb.append("{\"stream\":true");  // 启用流式响应

      // bot_id：优先用请求中的值，否则用配置文件默认值
      String botId = request.getBotId();
      if (botId == null || botId.isEmpty()) botId = this.botId;
      sb.append(",\"bot_id\":\"").append(this.escapeJson(botId)).append("\"");

      // user_id：优先用请求中的值，否则用 "anonymous"
      String userId = request.getUser();
      if (userId == null || userId.isEmpty()) userId = "anonymous";
      sb.append(",\"user_id\":\"").append(this.escapeJson(userId)).append("\"");

      // additional_messages：用户问题
      if (request.getQuery() != null && !request.getQuery().isEmpty()) {
         sb.append(",\"additional_messages\":[{\"role\":\"user\",\"content\":\"")
            .append(this.escapeJson(request.getQuery()))
            .append("\",\"content_type\":\"text\"}]");
      }

      // conversation_id：多轮对话上下文（可选）
      if (request.getConversationId() != null && !request.getConversationId().isEmpty()) {
         sb.append(",\"conversation_id\":\"").append(this.escapeJson(request.getConversationId())).append("\"");
      }

      sb.append("}");
      return sb.toString();
   }

   /**
    * JSON字符串转义 — 防止特殊字符破坏JSON结构
    * 处理：反斜杠、双引号、换行、回车、制表符
    */
   private String escapeJson(String s) {
      return s == null ? "" : s
         .replace("\\", "\\\\")   // 反斜杠转义
         .replace("\"", "\\\"")   // 双引号转义
         .replace("\n", "\\n")    // 换行转义
         .replace("\r", "\\r")    // 回车转义
         .replace("\t", "\\t");   // 制表符转义
   }
}
