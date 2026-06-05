package com.example.demo.demos.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PathVariableController {
   @RequestMapping(
      value = {"/user/{userId}/roles/{roleId}"},
      method = {RequestMethod.GET}
   )
   @ResponseBody
   public String getLogin(@PathVariable("userId") String userId, @PathVariable("roleId") String roleId) {
      return "User Id : " + userId + " Role Id : " + roleId;
   }

   @RequestMapping(
      value = {"/javabeat/{regexp1:[a-z-]+}"},
      method = {RequestMethod.GET}
   )
   @ResponseBody
   public String getRegExp(@PathVariable("regexp1") String regexp1) {
      return "URI Part : " + regexp1;
   }
}
