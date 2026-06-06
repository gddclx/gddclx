package com.example.game.mapper;

import org.apache.ibatis.annotations.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface GameSignMapper {

    @Insert("INSERT INTO game_sign_log (employee_id, sign_date, coins_earned) VALUES (#{employeeId}, #{signDate}, #{coinsEarned})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertSign(Map<String, Object> params);

    @Select("SELECT * FROM game_sign_log WHERE employee_id = #{employeeId} AND sign_date = #{signDate}")
    Map<String, Object> findByEmployeeIdAndDate(@Param("employeeId") String employeeId, @Param("signDate") LocalDate signDate);

    @Select("SELECT COUNT(*) FROM game_sign_log WHERE employee_id = #{employeeId}")
    int getSignCount(@Param("employeeId") String employeeId);
}
