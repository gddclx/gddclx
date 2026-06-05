package com.example.demo.mapper;

import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Map;

@Mapper
public interface GameRankMapper {

    @Select("SELECT eg.employee_id as employeeId, eg.coins, e.name " +
            "FROM employee_game eg " +
            "LEFT JOIN employee e ON eg.employee_id = e.employee_id " +
            "ORDER BY eg.coins DESC LIMIT 10")
    List<Map<String, Object>> findTop10WithNames();

    @Select("SELECT COUNT(*) + 1 FROM employee_game WHERE coins > (SELECT coins FROM employee_game WHERE employee_id = #{employeeId})")
    Long getRankByEmployeeId(@Param("employeeId") Long employeeId);

    @Select("SELECT eg.employee_id as employeeId, eg.coins, e.name " +
            "FROM employee_game eg " +
            "LEFT JOIN employee e ON eg.employee_id = e.employee_id " +
            "WHERE eg.employee_id = #{employeeId}")
    Map<String, Object> findByEmployeeIdWithName(@Param("employeeId") Long employeeId);
}
