package com.example.demo.domain;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class GameBankDaily {
    private Long id;
    private LocalDate bankDate;
    private BigDecimal hrRate;
    private BigDecimal rdRate;
    private BigDecimal salesRate;
    private BigDecimal pmHrRate;
    private BigDecimal pmRdRate;
    private BigDecimal pmSalesRate;
    private LocalDateTime createdAt;
}
