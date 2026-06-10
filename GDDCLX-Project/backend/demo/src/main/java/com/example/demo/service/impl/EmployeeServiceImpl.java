package com.example.demo.service.impl;

import com.example.demo.domain.Employee;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.mapper.EmployeeMapper;
import com.example.demo.service.EmployeeService;
import java.util.Base64;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 员工认证服务实现类
 * 提供登录验证和Token生成（简易Base64编码，非标准JWT）
 * 依赖：EmployeeMapper（数据库操作）
 *
 * 安全说明：
 * - 密码明文存储和比对（生产环境应使用BCryptPasswordEncoder）
 * - Token用Base64编码（非标准JWT，可被解码，生产环境应使用jjwt库+HMAC签名）
 */
@Service  // 标识为Spring Service Bean，由Spring容器管理生命周期
public class EmployeeServiceImpl implements EmployeeService {
   @Autowired
   private EmployeeMapper employeeMapper;  // MyBatis生成的代理实现类

   /**
    * 登录验证 — 查数据库 + 明文密码比对
    * @param employeeId 员工工号（如 A001）
    * @param password 明文密码
    * @return 验证通过返回Employee对象（含所有字段），失败返回null
    */
   public Employee login(String employeeId, String password) {
      Employee employee = this.employeeMapper.selectByEmployeeId(employeeId);  // 先查工号
      return employee != null && employee.getPassword().equals(password) ? employee : null;  // 明文比对
   }

   /**
    * 生成简易Token — Base64编码（非标准JWT）
    * 格式：Base64(employeeId + ":" + name + ":" + timestamp)
    * 示例输入："A001:管理员:1717228800000"
    * 示例输出："QTAwMTrnrqHnkIblkZg6MTcxNzIyODgwMDAwMA=="
    *
    * 注意：这只是Base64编码，不是真正的JWT（无签名段）
    * 前端用此Token作为登录凭证存入localStorage
    */
   public String generateToken(Employee employee) {
      String tokenData = employee.getEmployeeId() + ":" + employee.getName() + ":" + System.currentTimeMillis();
      return Base64.getEncoder().encodeToString(tokenData.getBytes());  // Base64编码
   }

   /**
    * 用户注册 — 创建新员工记录
    * 所有注册用户初始配置：role=employee, department=技术研发, position=未分配, status=在职
    * 管理员后续可在后台修改部门和岗位
    */
   public boolean register(RegisterRequest request) {
      // 校验确认密码（Controller已校验，这里二次校验）
      if (!request.getPassword().equals(request.getConfirmPassword())) {
         return false;
      } else {
         // 检查工号是否已存在
         Employee existEmployee = this.employeeMapper.selectByEmployeeId(request.getEmployeeId());
         if (existEmployee != null) {
            return false;  // 工号已存在
         } else {
            // 构建新员工对象，填默认值
            Employee employee = new Employee();
            employee.setEmployeeId(request.getEmployeeId());
            employee.setPassword(request.getPassword());
            employee.setName(request.getName());
            employee.setRole("employee");        // 新用户一律普通员工
            employee.setDepartment("技术研发");   // 默认部门
            employee.setPosition("未分配");      // 默认岗位
            employee.setStatus("在职");          // 默认状态
            employee.setCreateTime(new Date());
            employee.setUpdateTime(new Date());
            int result = this.employeeMapper.insert(employee);
            return result > 0;  // 影响行数>0表示成功
         }
      }
   }
}
