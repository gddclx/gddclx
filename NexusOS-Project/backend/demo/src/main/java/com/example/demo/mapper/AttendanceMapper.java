package com.example.demo.mapper;

import com.example.demo.domain.Attendance;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AttendanceMapper {
   int insert(Attendance var1);

   Attendance selectByEmployeeIdAndDateStr(@Param("employeeId") String var1, @Param("checkinDateStr") String var2);

   int countTodayCheckinStr(@Param("todayStr") String var1);

   List<Attendance> selectByEmployeeIdAndDateRange(@Param("employeeId") String var1, @Param("startDate") Date var2, @Param("endDate") Date var3);

   int countByEmployeeIdAndDateRange(@Param("employeeId") String var1, @Param("startDate") Date var2, @Param("endDate") Date var3);

   int countEmployeeTotal();

   List<Attendance> selectAll();
}
