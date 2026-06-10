package com.example.game.mapper;

import com.example.game.domain.GameInvestment;
import org.apache.ibatis.annotations.*;
import java.time.LocalDate;
import java.util.List;

/** 投资记录表 Mapper — game_investment 表 */
@Mapper
public interface GameInvestmentMapper {
    @Select("SELECT * FROM game_investment WHERE employee_id = #{employeeId} AND invest_date = #{investDate}")
    GameInvestment findByEmployeeIdAndDate(@Param("employeeId") String employeeId, @Param("investDate") LocalDate investDate);

    /** 查今日某时段的投资（用于防重复投资） */
    @Select("SELECT * FROM game_investment WHERE employee_id = #{employeeId} AND invest_date = #{investDate} AND period_type = #{periodType}")
    GameInvestment findByEmployeeIdDateAndPeriod(@Param("employeeId") String employeeId, @Param("investDate") LocalDate investDate, @Param("periodType") int periodType);

    /** 查某日所有未结算投资（定时结算用） */
    @Select("SELECT * FROM game_investment WHERE settled = 0 AND invest_date = #{investDate}")
    List<GameInvestment> findUnsettledByDate(@Param("investDate") LocalDate investDate);

    @Insert("INSERT INTO game_investment (employee_id, amount, option_type, invest_date, period_type, settled) " +
            "VALUES (#{employeeId}, #{amount}, #{optionType}, #{investDate}, #{periodType}, 0)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(GameInvestment investment);

    /** 结算投资 — 写入 result_coins + 标记settled=1 */
    @Update("UPDATE game_investment SET result_coins = #{resultCoins}, settled = 1 WHERE id = #{id}")
    int settle(@Param("id") Long id, @Param("resultCoins") Long resultCoins);

    /** 查上次已结算投资（显示上次投资结果） */
    @Select("SELECT * FROM game_investment WHERE employee_id = #{employeeId} AND settled = 1 ORDER BY invest_date DESC, period_type DESC LIMIT 1")
    GameInvestment findLastSettledInvestment(@Param("employeeId") String employeeId);

    /** 查某员工所有未结算投资（手动结算用） */
    @Select("SELECT * FROM game_investment WHERE employee_id = #{employeeId} AND settled = 0 ORDER BY invest_date ASC, period_type ASC")
    List<GameInvestment> findUnsettledByEmployeeId(@Param("employeeId") String employeeId);
}
