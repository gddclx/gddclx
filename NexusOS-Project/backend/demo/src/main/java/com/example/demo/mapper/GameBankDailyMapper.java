package com.example.demo.mapper;

import com.example.demo.domain.GameBankDaily;
import org.apache.ibatis.annotations.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Mapper
public interface GameBankDailyMapper {

    @Select("SELECT * FROM game_bank_daily WHERE bank_date = #{bankDate}")
    GameBankDaily findByDate(@Param("bankDate") LocalDate bankDate);

    @Insert("INSERT INTO game_bank_daily (bank_date, hr_rate, rd_rate, sales_rate) " +
            "VALUES (#{bankDate}, #{hrRate}, #{rdRate}, #{salesRate})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(GameBankDaily bankDaily);

    @Select("SELECT * FROM game_bank_daily WHERE bank_date >= #{startDate} ORDER BY bank_date ASC")
    List<GameBankDaily> findFromDate(@Param("startDate") LocalDate startDate);
}
