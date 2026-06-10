package com.example.game.mapper;

import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Map;

/** 排行榜查询 Mapper — LEFT JOIN employee 获取姓名 */
@Mapper
public interface GameRankMapper {
    /** TOP10 + 员工姓名（LEFT JOIN employee表） */
    @Select("SELECT eg.employee_id as employeeId, eg.coins, e.name " +
            "FROM employee_game eg " +
            "LEFT JOIN employee e ON eg.employee_id = e.employee_id " +
            "ORDER BY eg.coins DESC LIMIT 10")
    List<Map<String, Object>> findTop10WithNames();

    /** 当前用户排名 — 子查询：比自己金币多的人数+1 */
    @Select("SELECT COUNT(*) + 1 FROM employee_game WHERE coins > (SELECT coins FROM employee_game WHERE employee_id = #{employeeId})")
    Long getRankByEmployeeId(@Param("employeeId") String employeeId);

    /** 查当前用户 + 姓名 */
    @Select("SELECT eg.employee_id as employeeId, eg.coins, e.name " +
            "FROM employee_game eg " +
            "LEFT JOIN employee e ON eg.employee_id = e.employee_id " +
            "WHERE eg.employee_id = #{employeeId}")
    Map<String, Object> findByEmployeeIdWithName(@Param("employeeId") String employeeId);
}
