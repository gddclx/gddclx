package com.example.game.mapper;

import com.example.game.domain.GameCoinRecord;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface GameCoinRecordMapper {

    @Insert("INSERT INTO game_coin_record (employee_id, type, amount, balance_after, description, created_at) " +
            "VALUES (#{employeeId}, #{type}, #{amount}, #{balanceAfter}, #{description}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(GameCoinRecord record);

    @Select("SELECT * FROM game_coin_record WHERE employee_id = #{employeeId} ORDER BY created_at DESC LIMIT 10")
    List<GameCoinRecord> findLast10ByEmployeeId(@Param("employeeId") String employeeId);
}
