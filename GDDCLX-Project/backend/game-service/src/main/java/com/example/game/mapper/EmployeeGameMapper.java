package com.example.game.mapper;

import com.example.game.domain.EmployeeGame;
import org.apache.ibatis.annotations.*;

/** 员工游戏状态表 Mapper — employee_game 表 */
@Mapper
public interface EmployeeGameMapper {
    @Select("SELECT * FROM employee_game WHERE employee_id = #{employeeId}")
    EmployeeGame findByEmployeeId(@Param("employeeId") String employeeId);

    /** 新用户初始化：金币0, 等级0, 待领取0 */
    @Insert("INSERT INTO employee_game (employee_id, coins, coin_level, unclaimed_count, last_collect_time) " +
            "VALUES (#{employeeId}, 0, 0, 0, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(EmployeeGame employeeGame);

    /** 更新游戏状态（领取/升级后） */
    @Update("UPDATE employee_game SET coins = #{coins}, coin_level = #{coinLevel}, " +
            "unclaimed_count = #{unclaimedCount}, last_collect_time = #{lastCollectTime} " +
            "WHERE employee_id = #{employeeId}")
    int updateByEmployeeId(EmployeeGame employeeGame);

    /** 增加金币 — 原子操作 */
    @Update("UPDATE employee_game SET coins = coins + #{coins} WHERE employee_id = #{employeeId}")
    int addCoins(@Param("employeeId") String employeeId, @Param("coins") Long coins);

    /** 扣除金币 — AND coins >= #{coins} 防止扣成负数 */
    @Update("UPDATE employee_game SET coins = coins - #{coins} WHERE employee_id = #{employeeId} AND coins >= #{coins}")
    int subtractCoins(@Param("employeeId") String employeeId, @Param("coins") Long coins);

    @Update("UPDATE employee_game SET last_sign_date = #{lastSignDate}, sign_count = #{signCount} WHERE employee_id = #{employeeId}")
    int updateSignInfo(@Param("employeeId") String employeeId, @Param("lastSignDate") java.time.LocalDate lastSignDate, @Param("signCount") Integer signCount);

    /** 排行榜 TOP N */
    @Select("SELECT * FROM employee_game ORDER BY coins DESC LIMIT #{limit}")
    java.util.List<EmployeeGame> findTopN(@Param("limit") int limit);

    /** 查用户排名 — 用子查询统计比自己金币多的人数 */
    @Select("SELECT COUNT(*) FROM employee_game WHERE coins > (SELECT coins FROM employee_game WHERE employee_id = #{employeeId})")
    Long getRankByEmployeeId(@Param("employeeId") String employeeId);
}
