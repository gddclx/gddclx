package com.example.game.mapper;

import com.example.game.domain.EmployeeGame;
import org.apache.ibatis.annotations.*;

@Mapper
public interface EmployeeGameMapper {

    @Select("SELECT * FROM employee_game WHERE employee_id = #{employeeId}")
    EmployeeGame findByEmployeeId(@Param("employeeId") String employeeId);

    @Insert("INSERT INTO employee_game (employee_id, coins, coin_level, unclaimed_count, last_collect_time) " +
            "VALUES (#{employeeId}, 0, 0, 0, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(EmployeeGame employeeGame);

    @Update("UPDATE employee_game SET coins = #{coins}, coin_level = #{coinLevel}, " +
            "unclaimed_count = #{unclaimedCount}, last_collect_time = #{lastCollectTime} " +
            "WHERE employee_id = #{employeeId}")
    int updateByEmployeeId(EmployeeGame employeeGame);

    @Update("UPDATE employee_game SET coins = coins + #{coins} WHERE employee_id = #{employeeId}")
    int addCoins(@Param("employeeId") String employeeId, @Param("coins") Long coins);

    @Update("UPDATE employee_game SET coins = coins - #{coins} WHERE employee_id = #{employeeId} AND coins >= #{coins}")
    int subtractCoins(@Param("employeeId") String employeeId, @Param("coins") Long coins);

    @Update("UPDATE employee_game SET last_sign_date = #{lastSignDate}, sign_count = #{signCount} " +
            "WHERE employee_id = #{employeeId}")
    int updateSignInfo(@Param("employeeId") String employeeId, @Param("lastSignDate") java.time.LocalDate lastSignDate, @Param("signCount") Integer signCount);

    @Select("SELECT * FROM employee_game ORDER BY coins DESC LIMIT #{limit}")
    java.util.List<EmployeeGame> findTopN(@Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM employee_game WHERE coins > (SELECT coins FROM employee_game WHERE employee_id = #{employeeId})")
    Long getRankByEmployeeId(@Param("employeeId") String employeeId);
}
