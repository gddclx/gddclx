package com.example.demo.dto;

/**
 * Coze AI对话请求DTO — 前端 → 后端
 * POST /api/coze/chat 的请求体
 * 前端JSON: {"botId":"764...", "user":"A001", "query":"今天考勤怎么样？", "conversationId":"conv_xxx"}
 * 注意：botId 和 user 如果为空，Service层会使用默认值
 */
public class CozeChatRequest {
   /** Coze Bot ID（可为空，服务端有默认值 7645638589472276518） */
   private String botId;
   /** 用户标识（可为空，默认 "anonymous"） */
   private String user;
   /** 用户输入的自然语言问题 */
   private String query;
   /** 对话会话ID（用于多轮对话，可为空） */
   private String conversationId;

   public String getBotId() { return this.botId; }
   public String getUser() { return this.user; }
   public String getQuery() { return this.query; }
   public String getConversationId() { return this.conversationId; }

   public void setBotId(String botId) { this.botId = botId; }
   public void setUser(String user) { this.user = user; }
   public void setQuery(String query) { this.query = query; }
   public void setConversationId(String conversationId) { this.conversationId = conversationId; }
}
