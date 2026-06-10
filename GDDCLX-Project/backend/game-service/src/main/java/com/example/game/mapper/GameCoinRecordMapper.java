package com.example.game.mapper;

import com.example.game.domain.GameCoinRecord;
import org.apache.ibatis.annotations.*;
import java.util.List;

/** 金币流水记录表 Mapper — game_coin_record 表 */
@Mapper
public interface GameCoinRecordMapper {
    /** 插入流水 — created_at自动设NOW() */
    @Insert("INSERT INTO game_coin_record (employee_id, type, amount, balance_after, description, created_at) " +
            "VALUES (#{employeeId}, #{type}, #{amount}, #{balanceAfter}, #{description}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(GameCoinRecord record);

    /** 最近10条流水 */
    @Select("SELECT * FROM game_coin_record WHERE employee_id = #{employeeId} ORDER BY created_at DESC LIMIT 10")
    List<GameCoinRecord> findLast10ByEmployeeId(@Param("employeeId") String employeeId);
}
