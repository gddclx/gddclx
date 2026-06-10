package com.example.demo.mapper;

import com.example.demo.domain.Attendance;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 考勤表 MyBatis Mapper接口
 * SQL实现位于 src/main/resources/mapper/AttendanceMapper.xml
 * 使用XML方式配置（而非注解），支持复杂SQL
 * @Mapper 注解使MyBatis自动生成代理实现类
 */
@Mapper
public interface AttendanceMapper {
   /** 插入新考勤记录 */
   int insert(Attendance var1);

   /** 按工号+日期字符串查今日是否已打卡（用于防重复） */
   Attendance selectByEmployeeIdAndDateStr(@Param("employeeId") String var1, @Param("checkinDateStr") String var2);

   /** 按工号+Date日期查打卡记录 */
   Attendance selectByEmployeeIdAndDate(@Param("employeeId") String employeeId, @Param("date") Date date);

   /** 签退更新：写入 checkout_time + is_late + is_early */
   int updateCheckout(@Param("employeeId") String employeeId, @Param("isLate") int isLate, @Param("isEarly") int isEarly);

   /** 今日已打卡人数统计 */
   int countTodayCheckinStr(@Param("todayStr") String var1);

   /** 按日期范围查考勤记录（周/月统计用） */
   List<Attendance> selectByEmployeeIdAndDateRange(@Param("employeeId") String var1, @Param("startDate") Date var2, @Param("endDate") Date var3);

   /** 统计某员工在日期范围内的打卡天数 */
   int countByEmployeeIdAndDateRange(@Param("employeeId") String var1, @Param("startDate") Date var2, @Param("endDate") Date var3);

   /** 全公司在职员工总数 */
   int countEmployeeTotal();

   /** 全量查询（AI接口用） */
   List<Attendance> selectAll();

   /** 带迟到早退标记的全量查询 */
   List<Map<String, Object>> selectAllWithLateEarly();

   /** 统计某员工的总迟到/早退/扣款数（绩效计算用） */
   Map<String, Object> countTotalLateEarlyByEmployeeId(@Param("employeeId") String employeeId);

   /** 确认迟到扣款：SET late_confirmed=1, salary_deduct+=50 */
   int confirmLate(@Param("employeeId") String employeeId);

   /** 确认早退扣款：SET early_confirmed=1, salary_deduct+=50 */
   int confirmEarly(@Param("employeeId") String employeeId);

   /** 赦免迟到：SET late_confirmed=0, salary_deduct-=50 WHERE salary_deduct>=50 */
   int forgiveLate(@Param("employeeId") String employeeId);

   /** 赦免早退：SET early_confirmed=0, salary_deduct-=50 WHERE salary_deduct>=50 */
   int forgiveEarly(@Param("employeeId") String employeeId);

   /** 定时任务：自动标记昨天未签退者为早退 */
   int autoMarkEarlyLeave();
}
