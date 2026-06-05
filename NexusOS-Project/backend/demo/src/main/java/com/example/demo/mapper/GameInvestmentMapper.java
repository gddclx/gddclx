package com.example.demo.mapper;

import com.example.demo.domain.GameInvestment;
import org.apache.ibatis.annotations.*;
import java.time.LocalDate;
import java.util.List;

@Mapper
public interface GameInvestmentMapper {

    @Select("SELECT * FROM game_investment WHERE employee_id = #{employeeId} AND invest_date = #{investDate}")
    GameInvestment findByEmployeeIdAndDate(@Param("employeeId") Long employeeId, @Param("investDate") LocalDate investDate);

    @Select("SELECT * FROM game_investment WHERE employee_id = #{employeeId} AND invest_date = #{investDate} AND period_type = #{periodType}")
    GameInvestment findByEmployeeIdDateAndPeriod(@Param("employeeId") Long employeeId, @Param("investDate") LocalDate investDate, @Param("periodType") int periodType);

    @Select("SELECT * FROM game_investment WHERE settled = 0 AND invest_date = #{investDate}")
    List<GameInvestment> findUnsettledByDate(@Param("investDate") LocalDate investDate);

    @Insert("INSERT INTO game_investment (employee_id, amount, option_type, invest_date, period_type, settled) " +
            "VALUES (#{employeeId}, #{amount}, #{optionType}, #{investDate}, #{periodType}, 0)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(GameInvestment investment);

    @Update("UPDATE game_investment SET result_coins = #{resultCoins}, settled = 1 WHERE id = #{id}")
    int settle(@Param("id") Long id, @Param("resultCoins") Long resultCoins);

    @Select("SELECT gi.* FROM game_investment gi " +
            "INNER JOIN employee_game eg ON gi.employee_id = eg.employee_id " +
            "ORDER BY eg.coins DESC LIMIT #{limit}")
    List<GameInvestment> findTopNByCoins(@Param("limit") int limit);

    @Select("SELECT * FROM game_investment WHERE employee_id = #{employeeId} AND settled = 1 ORDER BY invest_date DESC, period_type DESC LIMIT 1")
    GameInvestment findLastSettledInvestment(@Param("employeeId") Long employeeId);
}
