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

@Service
public class CozeServiceImpl implements CozeService {
   private static final String COZE_URL = "https://api.coze.cn/v3/chat";

   @Value("${coze.api.token:}")
   private String apiToken;

   @Value("${coze.bot.id:7645638589472276518}")
   private String botId;

   private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

   public String chat(CozeChatRequest request) {
      HttpURLConnection conn = null;

      String line;
      try {
         URL url = new URL("https://api.coze.cn/v3/chat");
         conn = (HttpURLConnection)url.openConnection();
         conn.setRequestMethod("POST");
         conn.setRequestProperty("Authorization", "Bearer " + this.apiToken);
         conn.setRequestProperty("Content-Type", "application/json");
         conn.setDoOutput(true);
         conn.setConnectTimeout(30000);
         conn.setReadTimeout(120000);
         String body = this.buildRequestBody(request);
         System.out.println("=== Coze请求体 ===");
         System.out.println(body);
         OutputStream os = conn.getOutputStream();

         try {
            os.write(body.getBytes(StandardCharsets.UTF_8));
            os.flush();
         } catch (Throwable var24) {
            if (os != null) {
               try {
                  os.close();
               } catch (Throwable var23) {
                  var24.addSuppressed(var23);
               }
            }

            throw var24;
         }

         if (os != null) {
            os.close();
         }

         int responseCode = conn.getResponseCode();
         if (responseCode != 200) {
            throw new RuntimeException("Coze API returned " + responseCode);
         }

         StringBuilder answerBuilder = new StringBuilder();
         String currentEvent = "";
         BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));

         try {
            label225:
            while(true) {
               while(true) {
                  do {
                     if ((line = reader.readLine()) == null) {
                        break label225;
                     }
                  } while(line.trim().isEmpty());

                  System.out.println("原始数据: " + line);
                  if (line.startsWith("event:")) {
                     currentEvent = line.substring(6).trim();
                  } else {
                     String jsonStr = line;
                     if (jsonStr.startsWith("data:")) {
                        jsonStr = jsonStr.substring(5).trim();
                        if (!jsonStr.isEmpty() && !"[DONE]".equals(jsonStr)) {
                           JsonNode event;
                           try {
                              event = OBJECT_MAPPER.readTree(jsonStr);
                           } catch (Exception var25) {
                              Exception e = var25;
                              System.out.println("JSON解析失败: " + e.getMessage());
                              continue;
                           }

                           if ("conversation.message.delta".equals(currentEvent)) {
                              String content = event.path("content").asText();
                              if (content != null && !content.isEmpty()) {
                                 answerBuilder.append(content);
                              }
                           } else if ("conversation.chat.completed".equals(currentEvent)) {
                              System.out.println("对话完成");
                              break label225;
                           }
                        }
                     }
                  }
               }
            }
         } catch (Throwable var26) {
            try {
               reader.close();
            } catch (Throwable var22) {
               var26.addSuppressed(var22);
            }

            throw var26;
         }

         reader.close();
         String answer = answerBuilder.toString();
         if (!answer.isEmpty()) {
            line = answer;
            return line;
         }

         line = "智能体未返回有效回答";
      } catch (RuntimeException var27) {
         RuntimeException e = var27;
         throw e;
      } catch (Exception var28) {
         Exception e = var28;
         throw new RuntimeException("调用Coze失败: " + e.getMessage(), e);
      } finally {
         if (conn != null) {
            conn.disconnect();
         }

      }

      return line;
   }

   private String buildRequestBody(CozeChatRequest request) {
      StringBuilder sb = new StringBuilder();
      sb.append("{\"stream\":true");
      String botId = request.getBotId();
      if (botId == null || botId.isEmpty()) {
         botId = this.botId;
      }

      sb.append(",\"bot_id\":\"").append(this.escapeJson(botId)).append("\"");
      String userId = request.getUser();
      if (userId == null || userId.isEmpty()) {
         userId = "anonymous";
      }

      sb.append(",\"user_id\":\"").append(this.escapeJson(userId)).append("\"");
      if (request.getQuery() != null && !request.getQuery().isEmpty()) {
         sb.append(",\"additional_messages\":[{\"role\":\"user\",\"content\":\"").append(this.escapeJson(request.getQuery())).append("\",\"content_type\":\"text\"}]");
      }

      if (request.getConversationId() != null && !request.getConversationId().isEmpty()) {
         sb.append(",\"conversation_id\":\"").append(this.escapeJson(request.getConversationId())).append("\"");
      }

      sb.append("}");
      return sb.toString();
   }

   private String escapeJson(String s) {
      return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
   }
}
