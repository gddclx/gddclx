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

@RestController
@RequestMapping({"/api/coze"})
@CrossOrigin
public class CozeController {
   @Autowired
   private CozeService cozeService;

   @PostMapping({"/chat"})
   public Map<String, Object> chat(@RequestBody CozeChatRequest request) {
      Map<String, Object> result = new HashMap();

      try {
         String response = this.cozeService.chat(request);
         result.put("code", 200);
         result.put("success", true);
         result.put("data", response);
      } catch (Exception var4) {
         Exception e = var4;
         result.put("code", 500);
         result.put("success", false);
         result.put("message", e.getMessage());
      }

      return result;
   }
}
