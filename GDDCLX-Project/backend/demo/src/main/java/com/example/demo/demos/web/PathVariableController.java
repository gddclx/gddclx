package com.example.demo.demos.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @PathVariable 路径参数示例 Controller（脚手架生成，非业务代码）
 * 演示从URL路径中提取动态参数，这是项目中最常用的参数传递方式之一
 * 项目实际使用：AttendanceController 的 /today/{userId}、TaskController 的 /{id} 等
 *
 * 端点（均为示例）：
 * /user/{userId}/roles/{roleId}  → 演示多路径参数
 * /javabeat/{regexp1:[a-z-]+}  → 演示正则约束路径参数
 */
@Controller
public class PathVariableController {

   /**
    * 演示多级路径参数提取
    * GET /user/A001/roles/admin → "User Id : A001 Role Id : admin"
    * @PathVariable 提取 URL 中的 {userId} 和 {roleId} 占位符
    */
   @RequestMapping(
      value = {"/user/{userId}/roles/{roleId}"},  // 路径模板：{}为占位符
      method = {RequestMethod.GET}                 // 限定只接受GET请求
   )
   @ResponseBody
   public String getLogin(@PathVariable("userId") String userId,  // 从URL提取 userId
                          @PathVariable("roleId") String roleId) {  // 从URL提取 roleId
      return "User Id : " + userId + " Role Id : " + roleId;
   }

   /**
    * 演示路径参数正则约束
    * GET /javabeat/hello-world → 200 OK
    * GET /javabeat/Hello → 404（包含大写字母，不符合正则 [a-z-]+）
    * [a-z-]+ 表示只允许小写字母和连字符
    */
   @RequestMapping(
      value = {"/javabeat/{regexp1:[a-z-]+}"},  // 正则约束：只接受小写字母和横线
      method = {RequestMethod.GET}
   )
   @ResponseBody
   public String getRegExp(@PathVariable("regexp1") String regexp1) {
      return "URI Part : " + regexp1;
   }
}
