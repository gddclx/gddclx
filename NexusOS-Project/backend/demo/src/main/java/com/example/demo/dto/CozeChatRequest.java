package com.example.demo.dto;

public class CozeChatRequest {
   private String botId;
   private String user;
   private String query;
   private String conversationId;

   public String getBotId() {
      return this.botId;
   }

   public String getUser() {
      return this.user;
   }

   public String getQuery() {
      return this.query;
   }

   public String getConversationId() {
      return this.conversationId;
   }

   public void setBotId(String botId) {
      this.botId = botId;
   }

   public void setUser(String user) {
      this.user = user;
   }

   public void setQuery(String query) {
      this.query = query;
   }

   public void setConversationId(String conversationId) {
      this.conversationId = conversationId;
   }

}
